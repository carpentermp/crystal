package com.mpc.dlx.crystal;

@SuppressWarnings("WeakerAccess")
public class Node {

  private final int value;
  private Node upLeft;
  private Node upRight;
  private Node right;
  private Node downRight;
  private Node downLeft;
  private Node left;

  public Node(int value) {
    this.value = value;
  }

  public int value() {
    return value;
  }

  public void set(Node node, Direction direction) {
    switch (direction) {
      case Right:
        right = node;
        break;
      case DownRight:
        downRight = node;
        break;
      case DownLeft:
        downLeft = node;
        break;
      case Left:
        left = node;
        break;
      case UpLeft:
        upLeft = node;
        break;
      case UpRight:
        upRight = node;
        break;
    }
  }

  public Node get(Direction direction) {
    switch (direction) {
      case Right:
        return right;
      case DownRight:
        return downRight;
      case DownLeft:
        return downLeft;
      case Left:
        return left;
      case UpLeft:
        return upLeft;
      case UpRight:
        return upRight;
    }
    return null;
  }

}
