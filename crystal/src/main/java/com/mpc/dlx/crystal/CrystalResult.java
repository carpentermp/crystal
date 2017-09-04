package com.mpc.dlx.crystal;

import au.id.bjf.dlx.DLXResult;
import com.mpc.dlx.crystal.result.Bead;
import com.mpc.dlx.crystal.result.Placement;
import com.mpc.dlx.crystal.result.Result;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "squid:S1640", "squid:HiddenFieldCheck"})
public class CrystalResult {

  private final Crystal crystal;
  private final Molecule rootMolecule;
  private final List<Row> rows;
  private final String bucketName;
  private final List<Integer> adjacencyCounts;

  public CrystalResult(DLXResult dlxResult, Crystal crystal, Molecule rootMolecule, List<Row> allRows) {
    this.crystal = crystal;
    this.rootMolecule = rootMolecule;
    this.rows = convertResultToRows(dlxResult, allRows);
    this.bucketName = buildBucketName();
    this.adjacencyCounts = computeAdjacencyCounts();
  }

  public String getBucketName() {
    return this.bucketName;
  }

  public Result toResultBean() {
    Result result = new Result();
    result.setCrystal(crystal.getName());
    result.setMolecule(rootMolecule.getName());
    result.setPlacements(new ArrayList<>());
    result.setAdjacencyOrder(computeAdjacencyOrder(rootMolecule));
    for (Row row : rows) {
      if (row.isHole()) {
        continue;
      }
      result.getPlacements().add(buildPlacement(crystal, row));
    }
    result.setAdjacencyOrder(computeAdjacencyOrder(rootMolecule));
    result.setAdjacencyCounts(this.adjacencyCounts);
    return result;
  }

  private Placement buildPlacement(Crystal crystal, Row row) {
    Placement placement = new Placement();
    placement.setOrientation(row.getMolecule().getOrientation().name());
    placement.setBeads(buildBeads(crystal, row));
    return placement;
  }

  /**
   * builds the beads for a given molecule placement
   *
   * @param crystal the crystal
   * @param row     the result row
   * @return the list of beads for given molecule placement
   */
  private List<Bead> buildBeads(Crystal crystal, Row row) {
    List<Bead> beads = new ArrayList<>();
    Molecule molecule = row.getMolecule();
    int nodeId = row.getNodeId();
    Node startingNode = crystal.getNode(nodeId);
    for (int i = 0; i < molecule.size(); i++) {
      int beadId = i + 1;
      Node beadNode = molecule.getBeadNode(startingNode, beadId);
      Bead bead = new Bead();
      bead.setId(beadId + (molecule.getOrientation() == Orientation.Right ? 5 : 0));
      bead.setSiteId(beadNode.getId());
      bead.setCoordinates(crystal.getCoordinates(beadNode.getId()));
      beads.add(bead);
    }
    return beads;
  }

  private List<Row> convertResultToRows(DLXResult result, List<Row> allRows) {
    List<Row> resultRows = new ArrayList<>();
    Iterator<List<Object>> it = result.rows();
    while (it.hasNext()) {
      List<Integer> usedIds = it.next()
          .stream()
          .map(String::valueOf)
          .filter(s -> Character.isDigit(s.charAt(0)))
          .map(Integer::parseInt)
          .collect(Collectors.toList());
      resultRows.add(findRow(usedIds, allRows));
    }
    return resultRows;
  }

  private Row findRow(List<Integer> usedIds, List<Row> allRows) {
    return allRows.stream()
        .filter(r -> r.isThisRow(usedIds))
        .findAny()
        .orElse(null);
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
      // subtract the counts for adjacencies within the molecules--they don't count
      subtractAdjacencies(adjacencyCountMap, buildAdjacencyCountMap(crystal, Collections.singletonList(row)));
    }
    List<Integer> adjacencyCounts = new ArrayList<>();
    for (int i = 1; i <= rootMolecule.size(); i++) {
      for (int j = i; j <= rootMolecule.size(); j++) {
        Integer count = adjacencyCountMap.get(buildAdjacencyName(i, j));
        if (count == null) {
          count = 0;
        }
        adjacencyCounts.add(count);
      }
    }
    return adjacencyCounts;
  }

  static void subtractAdjacencies(Map<String, Integer> adjacencyCounts, Map<String, Integer> countsToSubtract) {
    for (Map.Entry<String, Integer> entry : countsToSubtract.entrySet()) {
      String key = entry.getKey();
      Integer countToSubtract = entry.getValue();
      Integer count = adjacencyCounts.get(key);
      if (count == null) {
        throw new IllegalArgumentException("Count did not exist!: " + key);
      }
      adjacencyCounts.put(key, count - countToSubtract);
    }
  }

  static Map<String, Integer> buildAdjacencyCountMap(Crystal crystal, List<Row> rows) {
    Map<Node, Integer> nodeToBeadIdMap = buildNodeToBeadIdMap(crystal, rows);
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
    String name = buildAdjacencyName(beadId1, beadId2);
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
  static Map<Node, Integer> buildNodeToBeadIdMap(Crystal crystal, List<Row> rows) {
    Map<Node, Integer> nodeToBeadIdMap = new HashMap<>();
    for (Row row : rows) {
      if (row.isHole()) {
        continue;
      }
      Molecule molecule = row.getMolecule();
      Node startingNode = crystal.getNode(row.getNodeId());
      for (int i = 0; i < molecule.size(); i++) {
        int beadId = i + 1;
        Node beadNode = molecule.getBeadNode(startingNode, beadId);
        nodeToBeadIdMap.put(beadNode, beadId);
      }
    }
    return nodeToBeadIdMap;
  }

  static List<String> computeAdjacencyOrder(Molecule molecule) {
    List<String> rtn = new ArrayList<>();
    int size = molecule.size();
    for (int i = 1; i <= size; i++) {
      for (int j = i; j <= size; j++) {
        rtn.add(buildAdjacencyName(i, j));
      }
    }
    return rtn;
  }

  static String buildAdjacencyName(int beadId1, int beadId2) {
    if (beadId1 > beadId2) {
      int temp = beadId1;
      beadId1 = beadId2;
      beadId2 = temp;
    }
    return beadId1 + "-" + beadId2;
  }

  public String getSuggestedFilenamePrefix() {
    return crystal.getName() + "_" + rootMolecule.getName() + "_" + getBucketName();
  }

  public String toString() {
    return getSuggestedFilenamePrefix() + ": " + Utils.join(adjacencyCounts, ", ");
  }

  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof CrystalResult)) {
      return false;
    }
    CrystalResult other = (CrystalResult) obj;
    return toString().equals(other.toString());
  }

  public int hashCode() {
    return toString().hashCode();
  }

}
