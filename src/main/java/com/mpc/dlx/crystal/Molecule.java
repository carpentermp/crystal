package com.mpc.dlx.crystal;

import java.util.*;

@SuppressWarnings({"WeakerAccess", "squid:S1168", "squid:S2386", "squid:S2259", "unused"})
public class Molecule {

  private static final int[] DEFAULT_BEAD_IDS = new int[] {1, 2, 3, 4, 5};
  private static final int[] M05_BEAD_IDS = new int[] {1, 4, 2, 3, 5};
  private static final int[] M10_BEAD_IDS = new int[] {1, 2, 4, 5, 3};
  private static final int[] M20_BEAD_IDS = new int[] {1, 2, 5, 3, 4};
  private static final int[] M22_BEAD_IDS = new int[] {1, 4, 2, 5, 3};

  // note: for molecules of different size this would naturally be different. But since all our molecules are of size 5, this works for all of them
  private static final List<String> ADJACENCY_ORDER = Arrays.asList(
    "1-1", "1-2", "1-3", "1-4", "1-5", "2-2", "2-3", "2-4", "2-5", "3-3", "3-4", "3-5", "4-4", "4-5", "5-5"
  );

  // order is:                                 1-1, 1-2, 1-3, 1-4, 1-5, 2-2, 2-3, 2-4, 2-5, 3-3, 3-4, 3-5, 4-4, 4-5, 5-5
  private static final int[] m01Adjacencies = {0,   1,   0,   0,   0,   0,   1,   0,   0,   0,   1,   0,   0,   1,   0};
  private static final int[] m02Adjacencies = {0,   1,   0,   0,   0,   0,   1,   0,   0,   0,   1,   1,   0,   1,   0};
  private static final int[] m03Adjacencies = {0,   1,   0,   0,   0,   0,   1,   0,   0,   0,   1,   0,   0,   1,   0};
  private static final int[] m04Adjacencies = {0,   1,   0,   0,   0,   0,   1,   1,   0,   0,   1,   1,   0,   1,   0};
  private static final int[] m05Adjacencies = {0,   1,   0,   1,   0,   0,   1,   1,   0,   0,   0,   1,   0,   0,   0};
  private static final int[] m06Adjacencies = {0,   1,   0,   0,   0,   0,   1,   1,   0,   0,   1,   0,   0,   1,   0};
  private static final int[] m07Adjacencies = {0,   1,   0,   0,   0,   0,   1,   0,   0,   0,   1,   1,   0,   1,   0};
  private static final int[] m08Adjacencies = {0,   1,   0,   0,   0,   0,   1,   0,   0,   0,   1,   0,   0,   1,   0};
  private static final int[] m09Adjacencies = {0,   1,   0,   0,   0,   0,   1,   0,   0,   0,   1,   0,   0,   1,   0};
  private static final int[] m10Adjacencies = {0,   1,   0,   0,   0,   0,   0,   1,   0,   0,   0,   1,   0,   1,   0};
  private static final int[] m11Adjacencies = {0,   1,   0,   0,   0,   0,   1,   0,   0,   0,   1,   1,   0,   0,   0};
  private static final int[] m12Adjacencies = {0,   1,   0,   0,   0,   0,   1,   0,   0,   0,   1,   0,   0,   1,   0};
  private static final int[] m13Adjacencies = {0,   1,   0,   0,   0,   0,   1,   1,   0,   0,   1,   0,   0,   1,   0};
  private static final int[] m14Adjacencies = {0,   1,   0,   0,   0,   0,   1,   0,   0,   0,   1,   0,   0,   1,   0};
  private static final int[] m15Adjacencies = {0,   1,   0,   0,   0,   0,   1,   0,   0,   0,   1,   0,   0,   1,   0};
  private static final int[] m16Adjacencies = {0,   1,   0,   0,   0,   0,   1,   1,   0,   0,   1,   0,   0,   1,   0};
  private static final int[] m17Adjacencies = {0,   1,   0,   0,   0,   0,   1,   0,   0,   0,   1,   0,   0,   1,   0};
  private static final int[] m18Adjacencies = {0,   1,   0,   0,   0,   0,   1,   0,   0,   0,   1,   0,   0,   1,   0};
  private static final int[] m19Adjacencies = {0,   1,   0,   0,   0,   0,   1,   0,   0,   0,   1,   1,   0,   0,   0};
  private static final int[] m20Adjacencies = {0,   1,   0,   0,   0,   0,   1,   1,   1,   0,   1,   1,   0,   0,   0};
  private static final int[] m21Adjacencies = {0,   1,   1,   0,   0,   0,   1,   1,   0,   0,   1,   1,   0,   1,   0};
  private static final int[] m22Adjacencies = {0,   1,   0,   1,   0,   0,   1,   1,   1,   0,   0,   1,   0,   0,   0};
  private static final int[] holAdjacencies = {0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0};

  public static final Molecule m01 = new Molecule("m01", m01Adjacencies, Direction.Right, Direction.Right, Direction.Right, Direction.DownRight);
  public static final Molecule m02 = new Molecule("m02", m02Adjacencies, Direction.Right, Direction.Right, Direction.Right, Direction.DownLeft);
  public static final Molecule m03 = new Molecule("m03", m03Adjacencies, Direction.Right, Direction.Right, Direction.DownRight, Direction.Right);
  public static final Molecule m04 = new Molecule("m04", m04Adjacencies, Direction.Right, Direction.Right, Direction.DownLeft, Direction.Right);
  public static final Molecule m05 = new Molecule("m05", m05Adjacencies, M05_BEAD_IDS, Orientation.Left, Direction.DownRight, Direction.UpRight, Direction.Right, Direction.DownRight);
  public static final Molecule m06 = new Molecule("m06", m06Adjacencies, Direction.Right, Direction.Right, Direction.DownLeft, Direction.DownRight);
  public static final Molecule m07 = new Molecule("m07", m07Adjacencies, Direction.Right, Direction.DownRight, Direction.Right, Direction.DownLeft);
  public static final Molecule m08 = new Molecule("m08", m08Adjacencies, Direction.DownRight, Direction.Right, Direction.Right, Direction.DownRight);
  public static final Molecule m09 = new Molecule("m09", m09Adjacencies, Direction.Right, Direction.Right, Direction.DownRight, Direction.DownLeft);
  public static final Molecule m10 = new Molecule("m10", m10Adjacencies, M10_BEAD_IDS, Orientation.Left, Direction.Right, Direction.DownRight, Direction.Right, Direction.UpRight);
  public static final Molecule m11 = new Molecule("m11", m11Adjacencies, Direction.Right, Direction.DownRight, Direction.Right, Direction.Back, Direction.DownLeft);
  public static final Molecule m12 = new Molecule("m12", m12Adjacencies, Orientation.Symmetric, Direction.Right, Direction.Right, Direction.Right, Direction.Right);
  public static final Molecule m13 = new Molecule("m13", m13Adjacencies, Orientation.AChiral, Direction.Right, Direction.DownRight, Direction.UpRight, Direction.Right);
  public static final Molecule m14 = new Molecule("m14", m14Adjacencies, Orientation.AChiral, Direction.Right, Direction.Right, Direction.DownRight, Direction.DownRight);
  public static final Molecule m15 = new Molecule("m15", m15Adjacencies, Orientation.AChiral, Direction.Right, Direction.DownRight, Direction.Right, Direction.DownRight);
  public static final Molecule m16 = new Molecule("m16", m16Adjacencies, Orientation.AChiral, Direction.Right, Direction.Right, Direction.DownLeft, Direction.DownLeft);
  public static final Molecule m17 = new Molecule("m17", m17Adjacencies, Orientation.AChiral, Direction.DownRight, Direction.Right, Direction.Right, Direction.UpRight);
  public static final Molecule m18 = new Molecule("m18", m18Adjacencies, Orientation.AChiral, Direction.Right, Direction.DownRight, Direction.DownLeft, Direction.Left);
  public static final Molecule m19 = new Molecule("m19", m19Adjacencies, Orientation.AChiral, Direction.Right, Direction.Right, Direction.DownRight, Direction.Back, Direction.UpRight);
  public static final Molecule m20 = new Molecule("m20", m20Adjacencies, M20_BEAD_IDS, Orientation.AChiral, Direction.Right, Direction.UpRight, Direction.DownRight, Direction.DownLeft);
  public static final Molecule m21 = new Molecule("m21", m21Adjacencies, Orientation.AChiral, Direction.DownRight, Direction.UpRight, Direction.DownRight, Direction.UpRight);
  public static final Molecule m22 = new Molecule("m22", m22Adjacencies, M22_BEAD_IDS, Orientation.Symmetric, Direction.DownRight, Direction.UpRight, Direction.UpRight, Direction.DownRight);

  public static final Molecule hole = new Molecule("hole", holAdjacencies, new int[] {}, Orientation.Circular);

  public static final Molecule[] allMolecules = new Molecule[] {
    m01, m02, m03, m04, m05, m06, m07, m08, m09, m10, m11, m12, m13, m14, m15, m16, m17, m18, m19, m20, m21, m22
  };

  private final String name;
  private final int[] internalAdjacencies;
  private final Orientation orientation;
  private final Direction rotation;
  private final Direction[] buildInstructions;
  private final int[] beadIds;

  public Molecule(String name, int[] internalAdjacencies, Direction... buildInstructions) {
    this(name, internalAdjacencies, Orientation.Left, buildInstructions);
  }

  private Molecule(String name, int[] internalAdjacencies, Orientation orientation, Direction... buildInstructions) {
    this(name, internalAdjacencies, DEFAULT_BEAD_IDS, orientation, buildInstructions);
  }

  private Molecule(String name, int[] internalAdjacencies, int[] beadIds, Orientation orientation, Direction... buildInstructions) {
    this(name, internalAdjacencies, beadIds, Direction.Right, orientation, buildInstructions);
  }

  private Molecule(String name, int[] internalAdjacencies, int[] beadIds, Direction rotation, Orientation orientation, Direction... buildInstructions) {
    this.name = name;
    this.internalAdjacencies = internalAdjacencies;
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
    return new Molecule(name, internalAdjacencies, beadIds, rotation.rotate(), orientation, rotatedInstructions);
  }

  public Molecule mirror(Direction axis) {
    Direction[] mirroredInstructions = new Direction[buildInstructions.length];
    for (int i = 0; i < mirroredInstructions.length; i++) {
      mirroredInstructions[i] = buildInstructions[i].mirror(axis);
    }
    return new Molecule(name, internalAdjacencies, beadIds, rotation, orientation.opposite(), mirroredInstructions);
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

  public List<String> getAdjacencyOrder() {
    return ADJACENCY_ORDER;
  }

  static String buildAdjacencyName(int beadId1, int beadId2) {
    if (beadId1 > beadId2) {
      int temp = beadId1;
      beadId1 = beadId2;
      beadId2 = temp;
    }
    return beadId1 + "-" + beadId2;
  }

  public void subtractInternalAdjacencies(Map<String, Integer> adjacencyCountMap) {
    List<String> adjacencyOrder = getAdjacencyOrder();
    if (adjacencyOrder.size() != internalAdjacencies.length) {
      throw new IllegalArgumentException("adjacencyOrder list is wrong size. Expected: " + internalAdjacencies.length + ", actual: " + adjacencyOrder.size());
    }
    for (int i = 0; i < internalAdjacencies.length; i++) {
      int internalCount = internalAdjacencies[i];
      if (internalCount != 0) {
        String key = adjacencyOrder.get(i);
        adjacencyCountMap.put(key, adjacencyCountMap.get(key) - internalCount);
      }
    }
  }

}
