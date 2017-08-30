package com.mpc.dlx.crystal;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@SuppressWarnings({"WeakerAccess", "squid:S1226", "squid:S1135", "squid:S106", "SameParameterValue"})
public class Crystal {

  private final Map<Integer, Node> nodes = new HashMap<>();
  // todo stuff for "holes" -- use "remainder". When > 0 then first hole goes at origin. Other holes float as one node molecules

  public Crystal(String neighborsFile) {
    try {
      Map<Integer, List<String>> connections = readInConnections(neighborsFile);
      for (Integer nodeId : connections.keySet()) {
        nodes.put(nodeId, new Node(nodeId));
      }
      for (Map.Entry<Integer, List<String>> entry  : connections.entrySet()) {
        Node node = nodes.get(entry.getKey());
        List<String> nodeConnections = entry.getValue();
        for (String line : nodeConnections) {
          String[] parts = line.split(" ");
          Direction direction = Direction.fromValue(Integer.parseInt(parts[1]));
          Integer connectingNode = Integer.parseInt(parts[2]);
          node.set(nodes.get(connectingNode), direction);
        }
      }
      // todo kludge. for now, just remove the 0th node
      removeNode(0);
      checkLinksBackAndForth();
      checkNoDuplicateNodes();
    }
    catch (IOException e) {
      throw new IllegalArgumentException(e);
    }
  }

  private void removeNode(int nodeId) {
    Node nodeZero = nodes.get(nodeId);
    Node rightNode = nodeZero.get(Direction.Right);
    Node downRightNode = nodeZero.get(Direction.DownRight);
    Node downLeftNode = nodeZero.get(Direction.DownLeft);
    Node leftNode = nodeZero.get(Direction.Left);
    Node upLeftNode = nodeZero.get(Direction.UpLeft);
    Node upRightNode = nodeZero.get(Direction.UpRight);
    rightNode.set(null, Direction.Left);
    downRightNode.set(null, Direction.UpLeft);
    downLeftNode.set(null, Direction.UpRight);
    leftNode.set(null, Direction.Right);
    upLeftNode.set(null, Direction.DownRight);
    upRightNode.set(null, Direction.DownLeft);
    nodes.remove(nodeId);
    checkDirection(rightNode, Direction.Right);
    checkDirection(downRightNode, Direction.DownRight);
    checkDirection(downLeftNode, Direction.DownLeft);
    checkDirection(leftNode, Direction.Left);
    checkDirection(upLeftNode, Direction.UpLeft);
    checkDirection(upRightNode, Direction.UpRight);
  }

  private void checkDirection(Node node, Direction direction) {
    Set<Integer> nodeIds = new HashSet<>(nodes.keySet());
    while (node != null) {
      nodeIds.remove(node.getId());
      node = node.get(direction);
    }
    if (!nodeIds.isEmpty()) {
      throw new IllegalArgumentException("Bad crystal!");
    }
  }

  private void checkNoDuplicateNodes() {
    for (Node node : nodes.values()) {
      int countWeShouldHave = 6;
      Set<Node> nodesAllAround = new HashSet<>();
      countWeShouldHave -= safeAddNode(nodesAllAround, node.get(Direction.Right)) ? 0 : 1;
      countWeShouldHave -= safeAddNode(nodesAllAround, node.get(Direction.DownRight)) ? 0 : 1;
      countWeShouldHave -= safeAddNode(nodesAllAround, node.get(Direction.DownLeft)) ? 0 : 1;
      countWeShouldHave -= safeAddNode(nodesAllAround, node.get(Direction.Left)) ? 0 : 1;
      countWeShouldHave -= safeAddNode(nodesAllAround, node.get(Direction.UpLeft)) ? 0 : 1;
      countWeShouldHave -= safeAddNode(nodesAllAround, node.get(Direction.UpRight)) ? 0 : 1;
      if (nodesAllAround.size() != countWeShouldHave) {
        throw new IllegalArgumentException("Found duplicate node neighbor!");
      }
      if (countWeShouldHave < 5) {
        throw new IllegalArgumentException("Too many nulls on a node");
      }
    }
  }

  private boolean safeAddNode(Set<Node> nodes, Node node) {
    if (node != null) {
      nodes.add(node);
      return true;
    }
    return false;
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

  private Map<Integer, List<String>> readInConnections(String neighborsFile) throws IOException {
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

  public Node getNode(int nodeId) {
    return nodes.get(nodeId);
  }

  public int size() {
    return nodes.size();
  }

  public Set<Integer> getNodeIds() {
    return Collections.unmodifiableSet(nodes.keySet());
  }

  public static void main(String[] args) {
    Crystal crystal = new Crystal("/Users/carpentermp/Downloads/neighbors.txt");
    System.out.println();
  }

}
