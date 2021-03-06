package com.mpc.dlx.crystal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "squid:S1226", "squid:S1135", "squid:S106", "SameParameterValue", "squid:HiddenFieldCheck", "squid:S3776", "squid:S1612"})
public class Crystal {

  private static final String NEIGHBORS_FILENAME = "neighbors.txt";
  private static final String COORDINATES_FILENAME = "replicated_coordinates.txt";
  private static final String NEIGHBORS_BY_ORIENTATION_FILENAME = "nbo.txt";
  private static final String MIDPOINTS_FILENAME = "midpoints.txt";

  private final File baseDir;
  private final String name;
  private final Map<Integer, Node> nodes = new HashMap<>();
  private final Map<Integer, List<Coordinate>> coordinates;
  private final int[][] nbo;
  // map of possible bonds to their index in the midpoints.txt file
  private final Map<BondKey, Integer> bondMap;
  private int holeCount;
  private final List<Integer> removedNodeIds = new ArrayList<>();

  public Crystal(String baseDirName, int moleculeSize) {
    this(new File(baseDirName), moleculeSize);
  }

  public Crystal(File baseDir, int moleculeSize) {
    this(baseDir, moleculeSize, null);
  }

  public Crystal(File baseDir, int moleculeSize, Symmetry symmetry) {
    this.name = computeName(baseDir, symmetry);
    this.baseDir = baseDir;
    try {
      Map<Integer, List<String>> connections = readInConnections();
      createNodes(connections);
      if (symmetry != null) {
        int holeCount = nodes.size() % (moleculeSize * symmetry.getRotationalSymmetry());
        for (Integer requiredHole : symmetry.getRequiredHoles()) {
          removeNode(getNode(requiredHole));
          holeCount--;
        }
        if (holeCount % symmetry.getRotationalSymmetry() != 0) {
          throw new IllegalStateException("Wrong number of holes for " + this.name + ". holes=" + holeCount + ". RotationalSymmetry=" + symmetry.getRotationalSymmetry() + ". No symmetrical solution of this kind is possible.");
        }
        this.holeCount = holeCount;
      }
      else {
        int holeCount = nodes.size() % moleculeSize;
        if (holeCount != 0) {
          removeNode(nodes.get(0));
          holeCount--;
          this.holeCount = holeCount;
        }
      }
      this.coordinates = readInCoordinates();
      this.nbo = readInNbo();
      this.bondMap = readInBondMap();
      checkLinksBackAndForth();
    }
    catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private void createNodes(Map<Integer, List<String>> connections) {
    for (Integer nodeId : connections.keySet()) {
      nodes.put(nodeId, new Node(nodeId));
    }
    for (Map.Entry<Integer, List<String>> entry : connections.entrySet()) {
      Node node = nodes.get(entry.getKey());
      List<String> nodeConnections = entry.getValue();
      for (String line : nodeConnections) {
        String[] parts = line.split(" ");
        Direction direction = Direction.fromValue(Integer.parseInt(parts[1]));
        Integer connectingNode = Integer.parseInt(parts[2]);
        node.set(nodes.get(connectingNode), direction);
      }
    }
  }

  static String computeName(File baseDir, Symmetry symmetry) {
    String baseName = "c" + baseDir.getName();
    return symmetry == null ? baseName : baseName + "_" + symmetry.getName();
  }

  private void removeNode(Node nodeToRemove) {
    Node rightNode = nodeToRemove.get(Direction.Right);
    Node downRightNode = nodeToRemove.get(Direction.DownRight);
    Node downLeftNode = nodeToRemove.get(Direction.DownLeft);
    Node leftNode = nodeToRemove.get(Direction.Left);
    Node upLeftNode = nodeToRemove.get(Direction.UpLeft);
    Node upRightNode = nodeToRemove.get(Direction.UpRight);
    rightNode.set(null, Direction.Left);
    downRightNode.set(null, Direction.UpLeft);
    downLeftNode.set(null, Direction.UpRight);
    leftNode.set(null, Direction.Right);
    upLeftNode.set(null, Direction.DownRight);
    upRightNode.set(null, Direction.DownLeft);
    nodes.remove(nodeToRemove.getId());
    removedNodeIds.add(nodeToRemove.getId());
  }

  /**
   * gets all the node ids (including removed nodes) as a sorted list of ids
   * @return all the node ids (including removed nodes) as a sorted list of ids
   */
  public List<Integer> getAllNodeIdsSorted() {
    List<Integer> allNodeIds = new ArrayList<>(removedNodeIds);
    allNodeIds.addAll(getNodeIds());
    return allNodeIds.stream()
      .sorted()
      .collect(Collectors.toList());
  }

  private void checkLinksBackAndForth() {
    for (Node node : nodes.values()) {
      checkLinksBackAndForth(node, Direction.Right);
      checkLinksBackAndForth(node, Direction.DownRight);
      checkLinksBackAndForth(node, Direction.DownLeft);
      checkLinksBackAndForth(node, Direction.Left);
      checkLinksBackAndForth(node, Direction.UpLeft);
      checkLinksBackAndForth(node, Direction.UpRight);
    }
  }

  private void checkLinksBackAndForth(Node node, Direction direction) {
    Node next = node.get(direction);
    if (next == null) {
      return;
    }
    if (node.getId() != next.get(direction.opposite()).getId()) {
      throw new IllegalArgumentException("Crystal links aren't set up right!");
    }
  }

  private Map<BondKey, Integer> readInBondMap() throws IOException {
    Map<BondKey, Integer> bondMap = new HashMap<>();
    String midpointsFilename = baseDir + "/" + MIDPOINTS_FILENAME;
    String line;
    int lineNumber = 0;
    try (BufferedReader reader = new BufferedReader(new FileReader(midpointsFilename))) {
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(" ");
        BondKey key = new BondKey(myParseInt(parts[0]), Direction.fromValue(myParseInt(parts[1])), myParseInt(parts[2]));
        bondMap.put(key, lineNumber++);
      }
    }
    return bondMap;
  }

  private int myParseInt(String string) {
    int index = string.contains(".") ? string.indexOf(".") : string.length();
    return Integer.parseInt(string.substring(0, index));
  }

  private int[][] readInNbo() {
    try {
      String nboFilename = baseDir + "/" + NEIGHBORS_BY_ORIENTATION_FILENAME;
      String line;
      List<int[]> rtn = new ArrayList<>();
      try (BufferedReader reader = new BufferedReader(new FileReader(nboFilename))) {
        while ((line = reader.readLine()) != null) {
          String[] parts = line.split(" ");
          int[] ints = new int[parts.length];
          for (int i = 0; i < ints.length; i++) {
            ints[i] = Integer.parseInt(parts[i]);
          }
          rtn.add(ints);
        }
      }
      return rtn.toArray(new int[rtn.size()][]);
    }
    catch (Exception e) {
      return null;
    }
  }

  private Map<Integer, List<String>> readInConnections() throws IOException {
    String neighborsFile = baseDir + "/" + NEIGHBORS_FILENAME;
    String line;
    try (BufferedReader reader = new BufferedReader(new FileReader(neighborsFile))) {
      Map<Integer, List<String>> connections = new HashMap<>();
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split(" ");
        if (parts.length < 3) {
          continue;
        }
        Integer nodeId = Integer.parseInt(parts[0]);
        List<String> nodeConnections = connections.computeIfAbsent(nodeId, k -> new ArrayList<>());
        nodeConnections.add(line);
      }
      return connections;
    }
  }

  private Map<Integer, List<Coordinate>> readInCoordinates() throws IOException {
    Map<Integer, List<Coordinate>> coordinates = new HashMap<>();
    String line;
    try (BufferedReader reader = new BufferedReader(new FileReader(baseDir + "/" + COORDINATES_FILENAME))) {
      while ((line = reader.readLine()) != null) {
        String[] parts = line.split("\t");
        if (parts.length < 4) {
          continue;
        }
        int nodeId = Integer.parseInt(parts[0]);
        Coordinate coordinate = new Coordinate(Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
        List<Coordinate> nodeCoordinates = coordinates.computeIfAbsent(nodeId, k -> new ArrayList<>());
        nodeCoordinates.add(coordinate);
      }
    }
    return coordinates;
  }

  public Node getNode(int nodeId) {
    return nodes.get(nodeId);
  }

  public int size() {
    return nodes.size();
  }

  public Set<Integer> getNodeIds() {
    return Collections.unmodifiableSet(nodes.keySet());
  }

  public List<String> getSortedNodeNames() {
    return getNodeIds().stream()
        .map(id -> Integer.toString(id))
        .sorted()
        .collect(Collectors.toList());
  }

  public String getName() {
    return name;
  }

  public int getHoleCount() {
    return holeCount;
  }

  public List<Coordinate> getCoordinates(int nodeId) {
    return coordinates.get(nodeId);
  }

  public int[][] getNeighborsByOrientation() {
    return nbo;
  }

  public Integer getBondKeyIndex(BondKey key) {
    return bondMap.get(key);
  }

}
