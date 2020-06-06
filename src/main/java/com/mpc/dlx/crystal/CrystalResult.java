package com.mpc.dlx.crystal;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"WeakerAccess", "squid:S1640", "squid:HiddenFieldCheck"})
public class CrystalResult {

  private static final String BUCKET_NAME_ALL = "all";

  private final Crystal crystal;
  private final Molecule rootMolecule;
  private final Molecule rootMolecule2;
  private final List<Row> rows;
  private final String bucketName;
  // map of node to the bead ID at that node (without "high" bead ids for right-hand oriented molecules)
  private final Map<Node, Integer> nodeToBeadIdsNoHigh = new HashMap<>();
  // map of node to the bead ID at that node (with "high" bead ids for right-hand oriented molecules)
  private final Map<Node, Integer> nodeToBeadIdsHigh = new HashMap<>();
  private final List<Integer> adjacencyCounts;
  private final String tag;
  private final ResultEquality equality;

  public CrystalResult(Crystal crystal, Molecule rootMolecule, Molecule rootMolecule2, List<Row> rows) {
    this.crystal = crystal;
    this.rootMolecule = rootMolecule;
    this.rootMolecule2 = rootMolecule2;
    this.rows = rows;
    this.bucketName = buildBucketName();
    buildNodeToBeadIdMaps();
    this.adjacencyCounts = computeAdjacencyCounts();
    this.tag = computeTag();
    this.equality = computeEquality();
  }

  public String getBucketName() {
    return this.bucketName;
  }

  public List<Row> getRows() {
    return Collections.unmodifiableList(rows);
  }

  public String getTag() {
    return tag;
  }

  public int getBeadId(int nodeId) {
    return getBeadId(crystal.getNode(nodeId));
  }

  public int getBeadId(Node node) {
    Integer beadId = nodeToBeadIdsHigh.get(node);
    return beadId == null ? 0 : beadId;
  }

  private String buildBucketName() {
    Map<Orientation, Integer> orientationCounts = countOrientations(rows);
    int leftCount = getCountOfOrientation(orientationCounts, Orientation.Left);
    int rightCount = getCountOfOrientation(orientationCounts, Orientation.Right);
    if (leftCount + rightCount == 0) {
      return BUCKET_NAME_ALL;
    }
    else if (rootMolecule2 != null && rootMolecule.getOrientation() == rootMolecule2.getOrientation()) {
      // if both molecules have the same orientation, then return the times each molecule was used
      String orientationLetter = rootMolecule.getOrientation().name().substring(0, 1).toLowerCase();
      Map<String, Integer> moleculeCounts = countMolecules(rows);
      return String.format(orientationLetter + "%1$02d" + orientationLetter + "%2$02d",
                           getCountOfMolecule(moleculeCounts, rootMolecule),
                           getCountOfMolecule(moleculeCounts, rootMolecule2));
    }
    return String.format("l%1$02dr%2$02d", leftCount, rightCount);
  }

  private Map<String, Integer> countMolecules(List<Row> resultRows) {
    Map<String, Integer> moleculeCounts = new HashMap<>();
    for (Row row : resultRows) {
      for (Molecule molecule : row.getMolecules()) {
        Integer count = moleculeCounts.get(molecule.getName());
        if (count == null) {
          count = 0;
        }
        moleculeCounts.put(molecule.getName(), ++count);
      }
    }
    return moleculeCounts;
  }

  private int getCountOfMolecule(Map<String, Integer> moleculeCounts, Molecule molecule) {
    Integer count = moleculeCounts.get(molecule.getName());
    return count == null ? 0 : count;
  }

  private Map<Orientation, Integer> countOrientations(List<Row> resultRows) {
    Map<Orientation, Integer> orientationCounts = new HashMap<>();
    for (Row row : resultRows) {
      for (Molecule molecule : row.getMolecules()) {
        Orientation orientation = molecule.getOrientation();
        Integer count = orientationCounts.get(orientation);
        if (count == null) {
          count = 0;
        }
        orientationCounts.put(orientation, ++count);
      }
    }
    return orientationCounts;
  }

  private int getCountOfOrientation(Map<Orientation, Integer> orientationCounts, Orientation orientation) {
    Integer count = orientationCounts.get(orientation);
    return count == null ? 0 : count;
  }

  /**
   * gets an ordered list of counts for the adjacencies between different beads
   *
   * @return an ordered list of counts for the adjacencies between different beads
   */
  private List<Integer> computeAdjacencyCounts() {
    Map<String, Integer> adjacencyCountMap = buildAdjacencyCountMap(rootMolecule2 == null ? nodeToBeadIdsNoHigh : nodeToBeadIdsHigh);
    for (Row row : rows) {
      if (!row.isHole()) {
        for (Molecule molecule : row.getMolecules()) {
          // subtract the counts for adjacencies within the molecules--they don't count
          molecule.subtractInternalAdjacencies(adjacencyCountMap, isHighMolecule(molecule));
        }
      }
    }
    List<Integer> adjacencyCounts = new ArrayList<>();
    for (int i = 1; i <= getAdjacencyBeadCount(); i++) {
      for (int j = i; j <= getAdjacencyBeadCount(); j++) {
        Integer count = adjacencyCountMap.get(Molecule.buildAdjacencyName(i, j));
        if (count == null) {
          count = 0;
        }
        adjacencyCounts.add(count);
      }
    }
    return adjacencyCounts;
  }

  private int getAdjacencyBeadCount() {
    return rootMolecule2 == null ? rootMolecule.size() : rootMolecule.size() + rootMolecule2.size();
  }

  private boolean isHighMolecule(Molecule molecule) {
    return rootMolecule2 != null && rootMolecule2.getName().equals(molecule.getName());
  }

  /**
   * build a map of adjacencyName e.g. "1-1" to count at that adjacency
   *
   * @param nodeToBeadIdMap map of nodes to the type of bead at that node (nodes without a bead in them i.e. holes are not in map)
   * @return the map of adjacencyName e.g. "1-1" to count at that adjacency
   */
  static Map<String, Integer> buildAdjacencyCountMap(Map<Node, Integer> nodeToBeadIdMap) {
    Map<String, Integer> adjacencyMap = new HashMap<>();
    for (Node node : nodeToBeadIdMap.keySet()) {
      for (int i = 1; i <= 6; i++) {
        addAdjacency(node, Direction.fromValue(i), nodeToBeadIdMap, adjacencyMap);
      }
    }
    for (String adjacencyName : adjacencyMap.keySet()) {
      int counts = adjacencyMap.get(adjacencyName);
      if ((counts & 1) != 0) {
        throw new IllegalStateException("Adjacency count was odd!");
      }
      adjacencyMap.put(adjacencyName, counts / 2);
    }
    return adjacencyMap;
  }

  // count an adjacency at a paticular location in a particular direction
  private static void addAdjacency(Node node, Direction direction, Map<Node, Integer> nodeToBeadIdMap, Map<String, Integer> adjacencyCounts) {
    Node otherNode = node.get(direction);
    if (otherNode == null) {
      return;
    }
    addAdjacency(nodeToBeadIdMap.get(node), nodeToBeadIdMap.get(otherNode), adjacencyCounts);
  }

  private static void addAdjacency(int beadId1, Integer beadId2, Map<String, Integer> adjacencyCounts) {
    if (beadId2 == null) {
      return;
    }
    String name = Molecule.buildAdjacencyName(beadId1, beadId2);
    Integer count = adjacencyCounts.get(name);
    if (count == null) {
      count = 0;
    }
    adjacencyCounts.put(name, ++count);
  }

  /**
   * build a maps of node to the bead id at that node (with ids both adjusted and not adjusted for right-hand molecules)
   */
  private void buildNodeToBeadIdMaps() {
    for (Row row : rows) {
      if (row.isHole()) {
        continue;
      }
      for (Molecule molecule : row.getMolecules()) {
        int rightHandOffset = computeRightHandOffset(molecule);
        Node startingNode = crystal.getNode(row.getNodeId(molecule));
        for (int i = 0; i < molecule.size(); i++) {
          int beadId = i + 1;
          Node beadNode = molecule.getBeadNode(startingNode, beadId);
          this.nodeToBeadIdsHigh.put(beadNode, beadId + rightHandOffset);
          this.nodeToBeadIdsNoHigh.put(beadNode, beadId);
        }
      }
    }
  }

  private int computeRightHandOffset(Molecule molecule) {
    if (rootMolecule2 == null) {
      return molecule.getOrientation() == Orientation.Right ? molecule.size() : 0;
    }
    return molecule.getName().equals(rootMolecule2.getName()) ? rootMolecule.size() : 0;
  }

  public List<Integer> getAdjacencyCounts() {
    return adjacencyCounts;
  }

  public String toString() {
    return crystal.getName() + "_" + CrystalResults.computeMoleculeDir(rootMolecule, rootMolecule2) + "_" + getBucketName() + ": " + adjacencyCounts + ", tag: " + tag;
  }

  public ResultEquality getEquality() {
    return equality;
  }

  public boolean equals(Object obj) {
    if (!(obj instanceof CrystalResult)) {
      return false;
    }
    CrystalResult other = (CrystalResult) obj;
    return equality.equals(other.getEquality());
  }

  public int hashCode() {
    return equality.hashCode();
  }

  private String computeTag() {
    List<String> trackList = getTrackList();
    List<String> regularTracks = trackList
      .stream()
      .map(Utils::smallestSubstring)
      .map(Utils::rotateOptimally)
      .distinct()
      .collect(Collectors.toList());
    List<String> chiralOppositeTracks = trackList
      .stream()
      .map(s -> chiralOpposite(s, rootMolecule.size()))
      .map(Utils::smallestSubstring)
      .map(Utils::rotateOptimally)
      .distinct()
      .collect(Collectors.toList());
    String partialTag = regularTracks.stream()
      .sorted()
      .collect(Collectors.joining());
    String chiralPartial = chiralOppositeTracks.stream()
      .sorted()
      .collect(Collectors.joining());
    return Stream.of(partialTag, chiralPartial).sorted().collect(Collectors.joining());
  }

  private static String chiralOpposite(String s, int moleculeSize) {
    StringBuilder sb = new StringBuilder();
    s.chars().forEach(ch -> sb.append(CrystalResult.chiralOpposite(ch, moleculeSize)));
    return sb.toString();
  }

  private static char chiralOpposite(int ch, int moleculeSize) {
    int beadId = ch - 'a';
    if (beadId == 0) {
      return (char) ch;
    }
    if (beadId > moleculeSize) {
      return (char) ('a' + (beadId - moleculeSize));
    }
    return (char) ('a' + beadId + moleculeSize);
  }

  private List<String> getTrackList() {
    int[][] nbo = crystal.getNeighborsByOrientation();
    List<String> trackList = new ArrayList<>();
    if (nbo == null) {
      return Collections.emptyList();
    }
    int width = nbo[0].length;
    int height = nbo.length - 2;
    for (int i = 0; i < width; i++) {
      StringBuilder sb = new StringBuilder();
      for (int j = 0; j < height; j++) {
        Node node = crystal.getNode(nbo[j + 1][i]);
        // if node is null, it's a hole
        Integer beadId = node == null ? 0 : nodeToBeadIdsHigh.get(node);
        sb.append((char) ('a' + (beadId == null ? 0 : beadId)));
      }
      trackList.add(sb.toString());
    }
    return trackList;
  }

  private ResultEquality computeEquality() {
    List<Byte> bytes = new ArrayList<>();
    adjacencyCounts.forEach(count -> bytes.add((byte) (count & 0xFF)));
    for (int i = 0; i < tag.length(); i += 2) {
      char ch2 = (i + 1 >= tag.length()) ? (char) ('a' + rootMolecule.size() * 2 + 1) : tag.charAt(i + 1);
      bytes.add(nibbleIt(tag.charAt(i), ch2));
    }
    byte[] rtn = new byte[bytes.size()];
    for (int i = 0; i < rtn.length; i++) {
      rtn[i] = bytes.get(i);
    }
    return new ResultEquality(rtn);
  }

  private byte nibbleIt(char ch1, char ch2) {
    int offset1 = ch1 - 'a';
    int offset2 = ch2 - 'a';
    int combined = (offset1 << 4) + offset2;
    return (byte) (combined & 0xFF);
  }

}
