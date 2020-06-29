package com.mpc.dlx.crystal;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"WeakerAccess", "squid:S1640", "squid:HiddenFieldCheck"})
public class CrystalResult {

  private static final String BUCKET_NAME_ALL = "all";

  private final Crystal crystal;
  private final RootMolecules rootMolecules;
  private final List<Row> rows;
  private final String bucketName;
  // map of node to the bead ID at that node (without "high" bead ids for enantiomers)
  private final Map<Node, Integer> nodeToAdjacencyBeadIds = new HashMap<>();
  // map of node to the bead ID at that node (with "high" bead ids for enantiomers)
  private final Map<Node, Integer> nodeToDistinctBeadIds = new HashMap<>();
  private final List<Integer> adjacencyCounts;
  private final String tag;
  private final ResultEquality equality;

  public CrystalResult(Crystal crystal, RootMolecules rootMolecules, List<Row> rows) {
    this.crystal = crystal;
    this.rootMolecules = rootMolecules;
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
    Integer beadId = nodeToDistinctBeadIds.get(node);
    return beadId == null ? 0 : beadId;
  }

  private String buildBucketName() {
    Map<Orientation, Integer> orientationCounts = countOrientations(rows);
    int leftCount = getCountOfOrientation(orientationCounts, Orientation.Left);
    int rightCount = getCountOfOrientation(orientationCounts, Orientation.Right);
    if (leftCount + rightCount == 0) {
      return BUCKET_NAME_ALL;
    }
    if (rootMolecules.twoMoleculesHaveSameSpecificOrientation()) {
      // if both molecules have the same orientation, then return the times each molecule was used
      String orientationLetter = getOrientationLetter(rootMolecules.getMolecule1());
      Map<String, Integer> moleculeCounts = countMolecules(rows);
      return String.format(orientationLetter + "%1$02d" + orientationLetter + "%2$02d",
                           getCountOfMolecule(moleculeCounts, rootMolecules.getMolecule1()),
                           getCountOfMolecule(moleculeCounts, rootMolecules.getMolecule2()));
    }
    if (rootMolecules.twoMoleculesWithEnantiomers()) {
      Map<String, Integer> countsMap = getCountsOfMoleculesWithOrientation(rows);
      String m1Name = rootMolecules.getMolecule1().getName();
      String m2Name = rootMolecules.getMolecule2().getName();
      Object[] counts = new Integer[4];
      counts[0] = safeGet(countsMap, m1Name + "l");
      counts[1] = safeGet(countsMap, m1Name + "r");
      counts[2] = safeGet(countsMap, m2Name + "l");
      counts[3] = safeGet(countsMap, m2Name + "r");
      StringBuilder sb = new StringBuilder();
      appendCount(sb, "l", 1);
      appendCount(sb, "r", 2);
      sb.append("_");
      appendCount(sb, "l", 3);
      appendCount(sb, "r", 4);
      return String.format(sb.toString(), counts);
    }
    return String.format("l%1$02dr%2$02d", leftCount, rightCount);
  }

  private static void appendCount(StringBuilder sb, String name, int parmNum) {
    sb.append(name).append("%").append(parmNum).append("$02d");
  }

  private static int safeGet(Map<String, Integer> counts, String name) {
    Integer count = counts.get(name);
    return count == null ? 0 : count;
  }

  private static String getNameWithOrientation(Molecule molecule) {
    return molecule.getName() + getOrientationLetter(molecule);
  }

  private static String getOrientationLetter(Molecule molecule) {
    return molecule.getOrientation().name().substring(0, 1).toLowerCase();
  }

  private void forEachMolecule(List<Row> rows, Consumer<Molecule> consumer) {
    for (Row row : rows) {
      if (!row.isHole()) {
        for (Molecule molecule : row.getMolecules()) {
          consumer.accept(molecule);
        }
      }
    }
  }

  private Map<String, Integer> countMolecules(List<Row> resultRows) {
    Map<String, Integer> moleculeCounts = new HashMap<>();
    forEachMolecule(resultRows, molecule -> {
      Integer count = moleculeCounts.get(molecule.getName());
      if (count == null) {
        count = 0;
      }
      moleculeCounts.put(molecule.getName(), ++count);
    });
    return moleculeCounts;
  }

  private int getCountOfMolecule(Map<String, Integer> moleculeCounts, Molecule molecule) {
    Integer count = moleculeCounts.get(molecule.getName());
    return count == null ? 0 : count;
  }

  private Map<String, Integer> getCountsOfMoleculesWithOrientation(List<Row> resultRows) {
    Map<String, Integer> rtn = new HashMap<>();
    forEachMolecule(resultRows, molecule -> {
      String name = getNameWithOrientation(molecule);
      Integer count = rtn.get(name);
      if (count == null) {
        count = 0;
      }
      rtn.put(name, ++count);
    });
    return rtn;
  }

  private Map<Orientation, Integer> countOrientations(List<Row> resultRows) {
    Map<Orientation, Integer> orientationCounts = new HashMap<>();
    forEachMolecule(resultRows, molecule -> {
      Orientation orientation = molecule.getOrientation();
      Integer count = orientationCounts.get(orientation);
      if (count == null) {
        count = 0;
      }
      orientationCounts.put(orientation, ++count);
    });
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
    Map<String, Integer> adjacencyCountMap = buildAdjacencyCountMap(nodeToAdjacencyBeadIds);
    forEachMolecule(rows, molecule -> {
      // subtract the counts for adjacencies within the molecules--they don't count
      molecule.subtractInternalAdjacencies(adjacencyCountMap, rootMolecules.isHighMolecule(molecule));
    });
    List<Integer> adjacencyCounts = new ArrayList<>();
    for (int i = 1; i <= rootMolecules.getAdjacencyBeadCount(); i++) {
      for (int j = i; j <= rootMolecules.getAdjacencyBeadCount(); j++) {
        Integer count = adjacencyCountMap.get(Molecule.buildAdjacencyName(i, j));
        if (count == null) {
          count = 0;
        }
        adjacencyCounts.add(count);
      }
    }
    return adjacencyCounts;
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
        int distinctBeadIdOffset = rootMolecules.getDistinctBeadIdOffset(molecule);
        int adjacencyBeadIdOffset = rootMolecules.getAdjacencyBeadIdOffset(molecule);
        Node startingNode = crystal.getNode(row.getNodeId(molecule));
        for (int beadId = 1; beadId <= molecule.size(); beadId++) {
          Node beadNode = molecule.getBeadNode(startingNode, beadId);
          this.nodeToDistinctBeadIds.put(beadNode, beadId + distinctBeadIdOffset);
          this.nodeToAdjacencyBeadIds.put(beadNode, beadId + adjacencyBeadIdOffset);
        }
      }
    }
  }

  public List<Integer> getAdjacencyCounts() {
    return adjacencyCounts;
  }

  public String toString() {
    return crystal.getName() + "_" + rootMolecules.getName() + "_" + getBucketName() + ": " + adjacencyCounts + ", tag: " + tag;
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
      // note: though this is not exactly right, it it matches what was done before
      // for the "2 molecules no enantiomers case". So to keep it the same as before
      // to avoid invalidating all of Johnny's computations of this kind
      .map(s -> chiralOpposite(s, rootMolecules.getDistinctBeadCount() / 2))
//      .map(s -> chiralOpposite(s, rootMolecules.getAdjacencyBeadCount()))
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

  private static String chiralOpposite(String s, int beadCountOffset) {
    StringBuilder sb = new StringBuilder();
    s.chars().forEach(ch -> sb.append(chiralOpposite(ch, beadCountOffset)));
    return sb.toString();
  }

  private static char chiralOpposite(int ch, int beadCountOffset) {
    int beadId = ch - 'a';
    if (beadId == 0) {
      return (char) ch;
    }
    if (beadId > beadCountOffset) {
      return (char) ('a' + (beadId - beadCountOffset));
    }
    return (char) ('a' + beadId + beadCountOffset);
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
        Integer beadId = node == null ? 0 : nodeToDistinctBeadIds.get(node);
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
      // adjacencyBeadCount * 2 + 1 ensures a character that is beyond any that would occur naturally in the string
      char ch2 = (i + 1 >= tag.length()) ? (char) ('a' + rootMolecules.getAdjacencyBeadCount() * 2 + 1) : tag.charAt(i + 1);
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
