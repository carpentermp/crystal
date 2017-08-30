package com.mpc.dlx.crystal;

import java.util.*;

@SuppressWarnings("WeakerAccess")
public class Molecule {

  public static final Molecule m01 = new Molecule(Direction.Right, Direction.Right, Direction.Right, Direction.DownRight);
  public static final Molecule m02 = new Molecule(Direction.Right, Direction.Right, Direction.Right, Direction.DownLeft);
  public static final Molecule m03 = new Molecule(Direction.Right, Direction.Right, Direction.DownRight, Direction.Right);
  public static final Molecule m04 = new Molecule(Direction.Right, Direction.Right, Direction.DownLeft, Direction.Right);
  public static final Molecule m05 = new Molecule(Direction.DownRight, Direction.UpRight, Direction.Right, Direction.DownRight); // numbers are different for this one
  public static final Molecule m06 = new Molecule(Direction.Right, Direction.Right, Direction.DownLeft, Direction.DownRight);
  public static final Molecule m07 = new Molecule(Direction.Right, Direction.DownRight, Direction.Right, Direction.DownLeft);
  public static final Molecule m08 = new Molecule(Direction.DownRight, Direction.Right, Direction.Right, Direction.DownRight);
  public static final Molecule m09 = new Molecule(Direction.Right, Direction.Right, Direction.DownRight, Direction.DownLeft);
  public static final Molecule m10 = new Molecule(Direction.Right, Direction.DownRight, Direction.Right, Direction.UpRight);
  public static final Molecule m11 = new Molecule(Direction.Right, Direction.DownRight, Direction.Right, Direction.Left, Direction.DownLeft); // backs up. Make a "back" movement?
  public static final Molecule m12 = new Molecule(Orientation.Symmetric, Direction.Right, Direction.Right, Direction.Right, Direction.Right);
  public static final Molecule m13 = new Molecule(Orientation.AChiral, Direction.Right, Direction.DownRight, Direction.UpRight, Direction.Right);
  public static final Molecule m14 = new Molecule(Orientation.AChiral, Direction.Right, Direction.Right, Direction.DownRight, Direction.DownRight);
  public static final Molecule m15 = new Molecule(Orientation.AChiral, Direction.Right, Direction.DownRight, Direction.Right, Direction.DownRight);
  public static final Molecule m16 = new Molecule(Orientation.AChiral, Direction.Right, Direction.Right, Direction.DownLeft, Direction.DownLeft);
  public static final Molecule m17 = new Molecule(Orientation.AChiral, Direction.DownRight, Direction.Right, Direction.Right, Direction.UpRight);
  public static final Molecule m18 = new Molecule(Orientation.AChiral, Direction.Right, Direction.DownRight, Direction.DownLeft, Direction.Left);
  public static final Molecule m19 = new Molecule(Orientation.AChiral, Direction.Right, Direction.Right, Direction.DownRight, Direction.UpLeft, Direction.UpRight); // backs up. Make "back" movement?
  public static final Molecule m20 = new Molecule(Orientation.AChiral, Direction.Right, Direction.UpRight, Direction.DownRight, Direction.DownLeft); // numbers are different
  public static final Molecule m21 = new Molecule(Orientation.AChiral, Direction.DownRight, Direction.UpRight, Direction.DownRight, Direction.UpRight);
  public static final Molecule m22 = new Molecule(Orientation.Symmetric, Direction.DownRight, Direction.UpRight, Direction.UpRight, Direction.DownRight); // numbers are different

  private final Orientation orientation;
  private final Direction[] buildInstructions;

  public Molecule(Direction... buildInstructions) {
    this(Orientation.Left, buildInstructions);
  }

  public Molecule(Orientation orientation, Direction... buildInstructions) {
    this.orientation = orientation;
    this.buildInstructions = buildInstructions;
  }

  public Molecule rotate() {
    Direction[] rotatedInstructions = new Direction[buildInstructions.length];
    for (int i = 0; i < rotatedInstructions.length; i++) {
      rotatedInstructions[i] = buildInstructions[i].rotate();
    }
    return new Molecule(orientation, rotatedInstructions);
  }

  public Molecule mirror(Direction axis) {
    Direction[] mirroredInstructions = new Direction[buildInstructions.length];
    for (int i = 0; i < mirroredInstructions.length; i++) {
      mirroredInstructions[i] = buildInstructions[i].mirror(axis);
    }
    return new Molecule(orientation.opposite(), mirroredInstructions);
  }

  public Orientation getOrientation() {
    return orientation;
  }

  public List<Direction> getDirections() {
    return Arrays.asList(buildInstructions);
  }

  public Set<Integer> getUsedNodeIds(Node startingNode) {
    Set<Integer> usedNodeIds = new HashSet<>();
    usedNodeIds.add(startingNode.value());
    Node next = startingNode;
    for (Direction direction : buildInstructions) {
      next = next.get(direction);
      if (next == null) {
        return null;
      }
      usedNodeIds.add(next.value());
    }
    return usedNodeIds;
  }

  public boolean equals(Object object) {
    if (object == null || !(object instanceof Molecule)) {
      return false;
    }
    Molecule other = (Molecule) object;
    if (orientation != other.orientation || buildInstructions.length != other.buildInstructions.length) {
      return false;
    }
    for (int i = 0; i < buildInstructions.length; i++) {
      if (buildInstructions[i] != other.buildInstructions[i]) {
        return false;
      }
    }
    return true;
  }

  public int hashCode() {
    List<Object> parts = new ArrayList<>(buildInstructions.length + 1);
    parts.add(orientation);
    Collections.addAll(parts, buildInstructions);
    return Objects.hash(parts.toArray(new Object[parts.size()]));
  }

}
