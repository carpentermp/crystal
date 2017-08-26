package com.mpc.dlx.crystal;

import java.util.*;

@SuppressWarnings("WeakerAccess")
public class Molecule {

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

}
