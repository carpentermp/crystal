package com.mpc.dlx.crystal;

import java.util.*;

@SuppressWarnings("WeakerAccess")
public class Crystal {

  private static final int[] downRight = new int[]{
      1, 3, 7, 12, 19, 26, 34, 41, 48, 53, 57, 59, 16, 23, 30, 6, 11, 18, 25, 33,
      40, 47, 52, 56, 10, 15, 22, 29, 37, 44, 17, 24, 32, 39, 46, 51, 5, 9, 14, 21,
      28, 36, 43, 50, 55, 31, 38, 45, 2, 4, 8, 13, 20, 27, 35, 42, 49, 54, 58, 60
  };

  private static final int[] downLeft = new int[]{
      23, 29, 36, 42, 48, 52, 5, 8, 12, 18, 24, 31, 60, 16, 22, 28, 35, 41, 47, 51,
      4, 7, 11, 17, 55, 58, 59, 15, 21, 27, 34, 40, 46, 2, 3, 6, 44, 50, 54, 57,
      10, 14, 20, 26, 33, 39, 45, 1, 30, 37, 43, 49, 53, 56, 9, 13, 19, 25, 32, 38
  };

  private static final int[] left = new int[]{
      16, 15, 14, 13, 12, 11, 44, 43, 42, 41, 40, 39, 38, 60, 59, 10, 9, 8, 7, 6,
      37, 36, 35, 34, 33, 32, 31, 58, 57, 56, 5, 4, 3, 30, 29, 28, 27, 26, 25, 24,
      55, 54, 53, 52, 51, 2, 1, 23, 22, 21, 20, 19, 18, 17, 50, 49, 48, 47, 46, 45
  };

  static Map<Integer, Direction> terminals = new HashMap<>();

  static {
    terminals.put(16, Direction.Right);
    terminals.put(60, Direction.DownRight);
    terminals.put(38, Direction.DownLeft);
    terminals.put(45, Direction.Left);
    terminals.put(1, Direction.UpLeft);
    terminals.put(23, Direction.UpRight);
  }

  private final Map<Integer, Node> nodes = new HashMap<>();

  public Crystal() {
    for (int i = 1; i <= 60; i++) {
      nodes.put(i, new Node(i));
    }
    checkArray(downRight, 60);
    checkArray(downLeft, 60);
    checkArray(left, 60);
    setUpLinks(downRight, Direction.DownRight);
    setUpLinks(downLeft, Direction.DownLeft);
    setUpLinks(left, Direction.Left);
    checkLinks();
  }

  public Node getNode(int nodeId) {
    return nodes.get(nodeId);
  }

  private void setUpLinks(int[] connections, Direction direction) {
    Node prev = null;
    for (int connection : connections) {
      Node node = nodes.get(connection);
      if (prev != null) {
        prev.set(node, direction);
        node.set(prev, direction.opposite());
      }
      prev = node;
    }
  }

  private static void checkArray(int[] arr, int size) {
    Set<Integer> ints = new HashSet<>();
    for (int anArr : arr) {
      ints.add(anArr);
    }
    if (ints.size() != size) {
      throw new IllegalArgumentException("Something wrong with array: " + Arrays.toString(arr) + ", size: " + ints.size() + ", initial size: " + arr.length);
    }
  }

  private void checkLinks() {
    for (Node node : nodes.values()) {
      checkNode(node, Direction.Right);
      checkNode(node, Direction.DownRight);
      checkNode(node, Direction.DownLeft);
      checkNode(node, Direction.Left);
      checkNode(node, Direction.UpLeft);
      checkNode(node, Direction.UpRight);
    }
  }

  private void checkNode(Node node, Direction direction) {
    if (node.get(direction) == null && terminals.get(node.value()) != direction) {
//      throw new IllegalArgumentException("Node had unexpected null direction. value: " + node.value() + " , direction: " + direction.name());
      System.out.println("Node had unexpected null direction. value: " + node.value() + " , direction: " + direction.name());
    }
  }

  public static void main(String[] args) {
    Crystal crystal = new Crystal();
    System.out.println();
  }

}
