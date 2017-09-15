package com.mpc.dlx.crystal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "squid:S1226", "squid:S1135", "squid:S106", "SameParameterValue", "squid:HiddenFieldCheck", "squid:S3776", "squid:S1612"})
public class Crystal {

  private static final String NEIGHBORS_FILENAME = "neighbors.txt";
  private static final String COORDINATES_FILENAME = "replicated_coordinates.txt";
  private static final int DEFAULT_MOLECULE_SIZE = 5;

  private final String name;
  private final Map<Integer, Node> nodes = new HashMap<>();
  private final Map<Integer, List<Coordinate>> coordinates;
  private int holeCount;
  private Node removedNode;

  public Crystal(String baseDir) {
    this(baseDir, DEFAULT_MOLECULE_SIZE, nameFromBaseDir(baseDir));
  }

  public Crystal(String baseDir, int moleculeSize, String name) {
    this.name = name;
    baseDir = baseDir + (baseDir.endsWith("/") ? "" : "/");
    try {
      Map<Integer, List<String>> connections = readInConnections(baseDir);
      createNodes(connections);
      int holeCount = nodes.size() % moleculeSize;
      if (holeCount != 0) {
        this.removedNode = nodes.get(0);
        removeNode(this.removedNode);
        holeCount--;
        this.holeCount = holeCount;
      }
      this.coordinates = readInCoordinates(baseDir);
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

  static String nameFromBaseDir(String baseDir) {
    if (baseDir.endsWith("/")) {
      baseDir = baseDir.substring(0, baseDir.lastIndexOf('/'));
    }
    return "c" + baseDir.substring(baseDir.lastIndexOf('/') + 1);
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
  }

  public Node getRemovedNode() {
    return removedNode;
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

  private Map<Integer, List<String>> readInConnections(String baseDir) throws IOException {
    String neighborsFile = baseDir + NEIGHBORS_FILENAME;
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

  private Map<Integer, List<Coordinate>> readInCoordinates(String baseDir) throws IOException {
    Map<Integer, List<Coordinate>> coordinates = new HashMap<>();
    String line;
    try (BufferedReader reader = new BufferedReader(new FileReader(baseDir + COORDINATES_FILENAME))) {
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

  public static void main(String[] args) {
    Crystal crystal = new Crystal("/Users/carpentermp/Downloads/textfiles/1372/");
    System.out.println("Crystal: " + crystal.getName());
  }

}
