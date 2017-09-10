package com.mpc.dlx.crystal;

@SuppressWarnings({"WeakerAccess", "squid:SwitchLastCaseIsDefaultCheck"})
public class Node {

  private final int id;
  private Node upLeft;
  private Node upRight;
  private Node right;
  private Node downRight;
  private Node downLeft;
  private Node left;

  public Node(int id) {
    this.id = id;
  }

  public int getId() {
    return id;
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
      default:
        throw new IllegalArgumentException("Invalid direction to set!");
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

  public boolean equals(Object object) {
    if (object == null || !(object instanceof Node)) {
      return false;
    }
    Node other = (Node) object;
    return id == other.id;
  }

  public int hashCode() {
    return Integer.hashCode(id);
  }

}
