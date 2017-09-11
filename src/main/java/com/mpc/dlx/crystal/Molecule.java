package com.mpc.dlx.crystal;

import java.util.*;

@SuppressWarnings({"WeakerAccess", "squid:S1168", "squid:S2386", "squid:S2259", "unused"})
public class Molecule {

  private static final int[] DEFAULT_BEAD_IDS = new int[] {1, 2, 3, 4, 5};
  private static final int[] M05_BEAD_IDS = new int[] {1, 4, 2, 3, 5};
  private static final int[] M20_BEAD_IDS = new int[] {1, 2, 5, 3, 4};
  private static final int[] M22_BEAD_IDS = new int[] {1, 4, 2, 5, 3};

  public static final Molecule m01 = new Molecule("m01", Direction.Right, Direction.Right, Direction.Right, Direction.DownRight);
  public static final Molecule m02 = new Molecule("m02", Direction.Right, Direction.Right, Direction.Right, Direction.DownLeft);
  public static final Molecule m03 = new Molecule("m03", Direction.Right, Direction.Right, Direction.DownRight, Direction.Right);
  public static final Molecule m04 = new Molecule("m04", Direction.Right, Direction.Right, Direction.DownLeft, Direction.Right);
  public static final Molecule m05 = new Molecule("m05", M05_BEAD_IDS, Orientation.Left, Direction.DownRight, Direction.UpRight, Direction.Right, Direction.DownRight);
  public static final Molecule m06 = new Molecule("m06", Direction.Right, Direction.Right, Direction.DownLeft, Direction.DownRight);
  public static final Molecule m07 = new Molecule("m07", Direction.Right, Direction.DownRight, Direction.Right, Direction.DownLeft);
  public static final Molecule m08 = new Molecule("m08", Direction.DownRight, Direction.Right, Direction.Right, Direction.DownRight);
  public static final Molecule m09 = new Molecule("m09", Direction.Right, Direction.Right, Direction.DownRight, Direction.DownLeft);
  public static final Molecule m10 = new Molecule("m10", Direction.Right, Direction.DownRight, Direction.Right, Direction.UpRight);
  public static final Molecule m11 = new Molecule("m11", Direction.Right, Direction.DownRight, Direction.Right, Direction.Back, Direction.DownLeft);
  public static final Molecule m12 = new Molecule("m12", Orientation.Symmetric, Direction.Right, Direction.Right, Direction.Right, Direction.Right);
  public static final Molecule m13 = new Molecule("m13", Orientation.AChiral, Direction.Right, Direction.DownRight, Direction.UpRight, Direction.Right);
  public static final Molecule m14 = new Molecule("m14", Orientation.AChiral, Direction.Right, Direction.Right, Direction.DownRight, Direction.DownRight);
  public static final Molecule m15 = new Molecule("m15", Orientation.AChiral, Direction.Right, Direction.DownRight, Direction.Right, Direction.DownRight);
  public static final Molecule m16 = new Molecule("m16", Orientation.AChiral, Direction.Right, Direction.Right, Direction.DownLeft, Direction.DownLeft);
  public static final Molecule m17 = new Molecule("m17", Orientation.AChiral, Direction.DownRight, Direction.Right, Direction.Right, Direction.UpRight);
  public static final Molecule m18 = new Molecule("m18", Orientation.AChiral, Direction.Right, Direction.DownRight, Direction.DownLeft, Direction.Left);
  public static final Molecule m19 = new Molecule("m19", Orientation.AChiral, Direction.Right, Direction.Right, Direction.DownRight, Direction.Back, Direction.UpRight);
  public static final Molecule m20 = new Molecule("m20", M20_BEAD_IDS, Orientation.AChiral, Direction.Right, Direction.UpRight, Direction.DownRight, Direction.DownLeft);
  public static final Molecule m21 = new Molecule("m21", Orientation.AChiral, Direction.DownRight, Direction.UpRight, Direction.DownRight, Direction.UpRight);
  public static final Molecule m22 = new Molecule("m22", M22_BEAD_IDS, Orientation.Symmetric, Direction.DownRight, Direction.UpRight, Direction.UpRight, Direction.DownRight);

  public static final Molecule hole = new Molecule("hole", new int[0], Orientation.Circular);

  public static final Molecule[] allMolecules = new Molecule[] {
    m01, m02, m03, m04, m05, m06, m07, m08, m09, m10, m11, m12, m13, m14, m15, m16, m17, m18, m19, m20, m21, m22
  };

  private final String name;
  private final Orientation orientation;
  private final Direction rotation;
  private final Direction[] buildInstructions;
  private final int[] beadIds;

  public Molecule(String name, Direction... buildInstructions) {
    this(name, Orientation.Left, buildInstructions);
  }

  private Molecule(String name, Orientation orientation, Direction... buildInstructions) {
    this(name, DEFAULT_BEAD_IDS, orientation, buildInstructions);
  }

  private Molecule(String name, int[] beadIds, Orientation orientation, Direction... buildInstructions) {
    this(name, beadIds, Direction.Right, orientation, buildInstructions);
  }

  private Molecule(String name, int[] beadIds, Direction rotation, Orientation orientation, Direction... buildInstructions) {
    this.name = name;
    this.beadIds = beadIds;
    this.orientation = orientation;
    this.rotation = rotation;
    this.buildInstructions = buildInstructions;
  }

  public Molecule rotate() {
    Direction[] rotatedInstructions = new Direction[buildInstructions.length];
    for (int i = 0; i < rotatedInstructions.length; i++) {
      rotatedInstructions[i] = buildInstructions[i].rotate();
    }
    return new Molecule(name, beadIds, rotation.rotate(), orientation, rotatedInstructions);
  }

  public Molecule mirror(Direction axis) {
    Direction[] mirroredInstructions = new Direction[buildInstructions.length];
    for (int i = 0; i < mirroredInstructions.length; i++) {
      mirroredInstructions[i] = buildInstructions[i].mirror(axis);
    }
    return new Molecule(name, beadIds, rotation, orientation.opposite(), mirroredInstructions);
  }

  public String getName() {
    return name;
  }

  public Orientation getOrientation() {
    return orientation;
  }

  public Direction getRotation() {
    return rotation;
  }

  public List<Direction> getDirections() {
    return Arrays.asList(buildInstructions);
  }

  int[] getBeadIds() {
    return this.beadIds;
  }

  @SuppressWarnings({"ForLoopReplaceableByForEach", "ConstantConditions"})
  public Node getBeadNode(Node startingNode, int beadId) {
    Node prev = null;
    Node next = startingNode;
    int directionIndex = 0;
    for (int i = 0; i < beadIds.length; i++) {
      if (beadId == beadIds[i]) {
        return next;
      }
      if (directionIndex >= buildInstructions.length) {
        break;
      }
      Direction nextDirection = buildInstructions[directionIndex++];
      if (nextDirection == Direction.Back) {
        nextDirection = buildInstructions[directionIndex++];
        next = prev;
      }
      prev = next;
      next = next.get(nextDirection);
    }
    throw new IllegalArgumentException("Bad bead id: " + beadId);
  }

  public int size() {
    return beadIds.length;
  }

  public Set<Integer> getUsedNodeIds(Node startingNode) {
    Set<Integer> usedNodeIds = new HashSet<>();
    usedNodeIds.add(startingNode.getId());
    Node prev = null;
    Node next = startingNode;
    for (Direction direction : buildInstructions) {
      if (direction == Direction.Back) {
        if (prev == null) {
          throw new IllegalArgumentException("Bad molecule build instructions--you can't back up until you go forward");
        }
        next = prev;
        continue;
      }
      prev = next;
      next = next.get(direction);
      if (next == null) {
        return null;
      }
      usedNodeIds.add(next.getId());
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

  public static Molecule fromNumber(int index) {
    if (index < 1 || index > allMolecules.length) {
      throw new IllegalArgumentException("Invalid molecule number: " + index);
    }
    return allMolecules[index - 1];
  }

}
