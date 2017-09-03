package com.mpc.dlx.crystal;

import au.id.bjf.dlx.DLXResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mpc.dlx.crystal.result.*;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "squid:S1640", "squid:HiddenFieldCheck"})
public class CrystalResult {

  private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

  private final Map<Orientation, Integer> orientationCounts;
  private final Crystal crystal;
  private final Molecule rootMolecule;
  private final List<Row> rows;
  private final Map<Node, Integer> nodeToBeadIdMap;

  public CrystalResult(DLXResult dlxResult, Crystal crystal, Molecule rootMolecule, List<Row> allRows) {
    this.rows = convertResultToRows(dlxResult, allRows);
    this.crystal = crystal;
    this.rootMolecule = rootMolecule;
    this.orientationCounts = countOrientations(rows);
    this.nodeToBeadIdMap = buildNodeToBeadMap();
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

  public String getBucketName() {
    int leftCount = getCountOfOrientation(Orientation.Left);
    int rightCount = getCountOfOrientation(Orientation.Right);
    if (leftCount + rightCount == 0) {
      return "all";
    }
    return String.format("l%1$02dr%2$02d", leftCount, rightCount);
  }

  private int getCountOfOrientation(Orientation orientation) {
    Integer count = orientationCounts.get(orientation);
    return count == null ? 0 : count;
  }

  public String toJson() {
    Result result = new Result();
    result.setCrystal(crystal.getName());
    result.setMolecule(rootMolecule.getName());
    result.setPlacements(new ArrayList<>());
    for (Row row : rows) {
      if (row.isHole()) {
        continue;
      }
      result.getPlacements().add(buildPlacement(crystal, row));
    }
    result.setAdjacencies(buildAdjacencies());
    return gson.toJson(result);
  }

  private List<Adjacency> buildAdjacencies() {
    Map<String, Integer> adjacencyCounts = new HashMap<>();
    for (Node node : nodeToBeadIdMap.keySet()) {
      for (int i = 0; i < 6; i++) {
        addAdjacency(node, Direction.fromValue(i + 1), nodeToBeadIdMap, adjacencyCounts);
      }
    }
    List<Adjacency> rtn = new ArrayList<>();
    for (Map.Entry<String, Integer> entry : adjacencyCounts.entrySet()) {
      Adjacency adjacency = new Adjacency();
      adjacency.setName(entry.getKey());
      adjacency.setCount(entry.getValue() / 2);
      rtn.add(adjacency);
      if ((entry.getValue() & 1) != 0) {
        throw new IllegalStateException("Adjacency count was odd!");
      }
    }
    rtn.sort(Comparator.comparing(Adjacency::getName));
    return rtn;
  }

  private void addAdjacency(Node node, Direction direction, Map<Node, Integer> nodeToBeadIdMap, Map<String, Integer> adjacencyCounts) {
    Node otherNode = node.get(direction);
    if (otherNode == null) {
      return;
    }
    addAdjacency(nodeToBeadIdMap.get(node), nodeToBeadIdMap.get(otherNode), adjacencyCounts);
  }

  private void addAdjacency(int beadId1, Integer beadId2, Map<String, Integer> adjacencyCounts) {
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

  private String buildAdjacencyName(int beadId1, int beadId2) {
    if (beadId1 > beadId2) {
      int temp = beadId1;
      beadId1 = beadId2;
      beadId2 = temp;
    }
    return beadId1 + "-" + beadId2;
  }

  private Placement buildPlacement(Crystal crystal, Row row) {
    Placement placement = new Placement();
    placement.setOrientation(row.getMolecule().getOrientation().name());
    placement.setBeads(buildBeads(crystal, row));
    return placement;
  }

  /**
   * builds the beads for a given molecule placement
   * @param crystal the crystal
   * @param row the result row
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

  /**
   * build a map of node to the bead at that node
   * @return map of node to the bead at that node
   */
  private Map<Node, Integer> buildNodeToBeadMap() {
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

}
