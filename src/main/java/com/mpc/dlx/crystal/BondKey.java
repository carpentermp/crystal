package com.mpc.dlx.crystal;

/**
 * represents an intra-molecular bond between two atoms, as placed upon the Crystal unit cell.
 */
@SuppressWarnings("WeakerAccess")
public class BondKey {

  private final int fromNodeId;
  private final Direction direction;
  private final int toNodeId;

  public BondKey(int fromNodeId, Direction direction, int toNodeId) {
    if (fromNodeId > toNodeId) {
      int temp = fromNodeId;
      fromNodeId = toNodeId;
      toNodeId = temp;
      direction = direction.opposite();
    }
    this.fromNodeId = fromNodeId;
    this.direction = direction;
    this.toNodeId = toNodeId;
  }

  public int getFromNodeId() {
    return fromNodeId;
  }

  public Direction getDirection() {
    return direction;
  }

  public int getToNodeId() {
    return toNodeId;
  }

  public String toString() {
    return fromNodeId + "-" + direction.value() + "-" + toNodeId;
  }

  public boolean equals(Object o) {
    return o instanceof BondKey && toString().equals(o.toString());
  }

  public int hashCode() {
    return toString().hashCode();
  }

}
