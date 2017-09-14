package com.mpc.dlx.crystal;

import java.util.*;

@SuppressWarnings({"WeakerAccess", "squid:S1640", "squid:HiddenFieldCheck"})
public class CrystalResult {

  private final Crystal crystal;
  private final Molecule rootMolecule;
  private final List<Row> rows;
  private final String bucketName;
  private final List<Integer> adjacencyCounts;
  private final boolean deduplicateResults;
  private final String equalsString;

  public CrystalResult(Crystal crystal, Molecule rootMolecule, List<Row> rows, boolean deduplicateResults) {
    this.crystal = crystal;
    this.rootMolecule = rootMolecule;
    this.deduplicateResults = deduplicateResults;
    this.rows = rows;
    this.bucketName = buildBucketName();
    this.adjacencyCounts = computeAdjacencyCounts();
    // compute this once for performance
    this.equalsString = crystal.getName() + "_" + rootMolecule.getName() + "_" + getBucketName() + ": " + Utils.join(adjacencyCounts, ", ");
  }

  public String getBucketName() {
    return this.bucketName;
  }

  public List<Row> getRows() {
    return Collections.unmodifiableList(rows);
  }

  private String buildBucketName() {
    Map<Orientation, Integer> orientationCounts = countOrientations(rows);
    int leftCount = getCountOfOrientation(orientationCounts, Orientation.Left);
    int rightCount = getCountOfOrientation(orientationCounts, Orientation.Right);
    if (leftCount + rightCount == 0) {
      return "all";
    }
    return String.format("l%1$02dr%2$02d", leftCount, rightCount);
  }

  private Map<Orientation, Integer> countOrientations(List<Row> resultRows) {
    Map<Orientation, Integer> orientationCounts = new HashMap<>();
    for (Row row : resultRows) {
      Orientation orientation = row.getMolecule().getOrientation();
      Integer count = orientationCounts.get(orientation);
      if (count == null) {
        count = 0;
      }
      orientationCounts.put(orientation, ++count);
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
    Map<String, Integer> adjacencyCountMap = buildAdjacencyCountMap(crystal, rows);
    for (Row row : rows) {
      if (!row.isHole()) {
        // subtract the counts for adjacencies within the molecules--they don't count
        row.getMolecule().subtractInternalAdjacencies(adjacencyCountMap);
      }
    }
    List<Integer> adjacencyCounts = new ArrayList<>();
    for (int i = 1; i <= rootMolecule.size(); i++) {
      for (int j = i; j <= rootMolecule.size(); j++) {
        Integer count = adjacencyCountMap.get(Molecule.buildAdjacencyName(i, j));
        if (count == null) {
          count = 0;
        }
        adjacencyCounts.add(count);
      }
    }
    return adjacencyCounts;
  }

  static Map<String, Integer> buildAdjacencyCountMap(Crystal crystal, List<Row> rows) {
    Map<Node, Integer> nodeToBeadIdMap = buildNodeToBeadIdMap(crystal, rows, false);
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
   * build a map of node to the bead at that node
   *
   * @return map of node to the bead at that node
   */
  public static Map<Node, Integer> buildNodeToBeadIdMap(Crystal crystal, List<Row> rows, boolean doRightHandOffset) {
    Map<Node, Integer> nodeToBeadIdMap = new HashMap<>();
    for (Row row : rows) {
      if (row.isHole()) {
        continue;
      }
      Molecule molecule = row.getMolecule();
      int rightHandOffset = 0;
      if (doRightHandOffset && molecule.getOrientation() == Orientation.Right) {
        rightHandOffset = molecule.size();
      }
      Node startingNode = crystal.getNode(row.getNodeId());
      for (int i = 0; i < molecule.size(); i++) {
        int beadId = i + 1;
        Node beadNode = molecule.getBeadNode(startingNode, beadId);
        nodeToBeadIdMap.put(beadNode, beadId + rightHandOffset);
      }
    }
    return nodeToBeadIdMap;
  }

  public List<Integer> getAdjacencyCounts() {
    return adjacencyCounts;
  }

  public String toString() {
    return equalsString;
  }

  public boolean equals(Object obj) {
    if (!deduplicateResults) {
      return super.equals(obj);
    }
    if (obj == null || !(obj instanceof CrystalResult)) {
      return false;
    }
    CrystalResult other = (CrystalResult) obj;
    return toString().equals(other.toString());
  }

  public int hashCode() {
    if (!deduplicateResults) {
      return super.hashCode();
    }
    return toString().hashCode();
  }

}
