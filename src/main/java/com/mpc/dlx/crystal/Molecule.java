package com.mpc.dlx.crystal;

import java.util.*;

@SuppressWarnings({"WeakerAccess", "squid:S1168", "squid:S2386", "squid:S2259", "unused"})
public class Molecule {

  private static final int[] DEFAULT_BEAD_IDS = new int[] {1, 2, 3, 4, 5};
  private static final int[] DIMER_BEAD_IDS = new int[] {1, 2};
  private static final int[] M05_BEAD_IDS = new int[] {1, 4, 2, 3, 5};
  private static final int[] M10_BEAD_IDS = new int[] {1, 2, 4, 5, 3};
  private static final int[] M20_BEAD_IDS = new int[] {1, 2, 5, 3, 4};
  private static final int[] M22_BEAD_IDS = new int[] {1, 4, 2, 5, 3};

  // note: for molecules of different size this would naturally be different. But since all our molecules are of size 5, this works for all of them
  private static final List<String> ADJACENCY_ORDER = Arrays.asList(
    "1-1", "1-2", "1-3", "1-4", "1-5", "2-2", "2-3", "2-4", "2-5", "3-3", "3-4", "3-5", "4-4", "4-5", "5-5"
  );

  private static final List<String> HIGH_ADJACENCY_ORDER = Arrays.asList(
    "6-6", "6-7", "6-8", "6-9", "6-10", "7-7", "7-8", "7-9", "7-10", "8-8", "8-9", "8-10", "9-9", "9-10", "10-10"
  );

  private static final List<String> INTER_ADJACENCY_ORDER = Arrays.asList(
    "1-1", "1-2", "1-3", "1-4", "1-5", "1-6", "1-7", "1-8", "1-9", "1-10",
    "2-2", "2-3", "2-4", "2-5", "2-6", "2-7", "2-8", "2-9", "2-10",
    "3-3", "3-4", "3-5", "3-6", "3-7", "3-8", "3-9", "3-10",
    "4-4", "4-5", "4-6", "4-7", "4-8", "4-9", "4-10",
    "5-5", "5-6", "5-7", "5-8", "5-9", "5-10",
    "6-6", "6-7", "6-8", "6-9", "6-10",
    "7-7", "7-8", "7-9", "7-10",
    "8-8", "8-9", "8-10",
    "9-9", "9-10",
    "10-10"
  );

  // note: for molecules of different size this would naturally be different. But since all our molecules are of size 5, this works for all of them
  private static final List<String> DIMER_ADJACENCY_ORDER = Arrays.asList(
    "1-1", "1-2", "2-2"
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
  private static final int[] dimerAdjacencies = {0,   1,   0};
  private static final int[] holeAdjacencies= {0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0,   0};

  public static final Molecule m01 = new Molecule("m01", m01Adjacencies, Direction.Right, Direction.Right, Direction.Right, Direction.DownRight);
  public static final Molecule m02 = new Molecule("m02", m02Adjacencies, Direction.Right, Direction.Right, Direction.Right, Direction.DownLeft, Direction.UpLeft);
  public static final Molecule m03 = new Molecule("m03", m03Adjacencies, Direction.Right, Direction.Right, Direction.DownRight, Direction.Right);
  public static final Molecule m04 = new Molecule("m04", m04Adjacencies, Direction.Right, Direction.Right, Direction.DownLeft, Direction.Right, Direction.UpLeft, Direction.Back, Direction.Back, Direction.UpLeft);
  public static final Molecule m05 = new Molecule("m05", m05Adjacencies, M05_BEAD_IDS, Orientation.Left, Direction.DownRight, Direction.UpRight, Direction.Right, Direction.DownRight, Direction.Back, Direction.Back, Direction.Left);
  public static final Molecule m06 = new Molecule("m06", m06Adjacencies, Direction.Right, Direction.Right, Direction.DownLeft, Direction.DownRight, Direction.Back, Direction.UpLeft);
  public static final Molecule m07 = new Molecule("m07", m07Adjacencies, Direction.Right, Direction.DownRight, Direction.Right, Direction.DownLeft, Direction.UpLeft);
  public static final Molecule m08 = new Molecule("m08", m08Adjacencies, Direction.DownRight, Direction.Right, Direction.Right, Direction.DownRight);
  public static final Molecule m09 = new Molecule("m09", m09Adjacencies, Direction.Right, Direction.Right, Direction.DownRight, Direction.DownLeft);
  public static final Molecule m10 = new Molecule("m10", m10Adjacencies, M10_BEAD_IDS, Orientation.Left, Direction.Right, Direction.DownRight, Direction.Right, Direction.UpRight);
  public static final Molecule m11 = new Molecule("m11", m11Adjacencies, Direction.Right, Direction.DownRight, Direction.Right, Direction.Back, Direction.DownLeft);
  public static final Molecule m12 = new Molecule("m12", m12Adjacencies, Orientation.AChiral, Direction.Right, Direction.Right, Direction.Right, Direction.Right);
  public static final Molecule m13 = new Molecule("m13", m13Adjacencies, Direction.Right, Direction.DownRight, Direction.UpRight, Direction.Right, Direction.Back, Direction.Left);
  public static final Molecule m14 = new Molecule("m14", m14Adjacencies, Direction.Right, Direction.Right, Direction.DownRight, Direction.DownRight);
  public static final Molecule m15 = new Molecule("m15", m15Adjacencies, Direction.Right, Direction.DownRight, Direction.Right, Direction.DownRight);
  public static final Molecule m16 = new Molecule("m16", m16Adjacencies, Direction.Right, Direction.Right, Direction.DownLeft, Direction.DownLeft, Direction.Back, Direction.UpLeft);
  public static final Molecule m17 = new Molecule("m17", m17Adjacencies, Direction.DownRight, Direction.Right, Direction.Right, Direction.UpRight);
  public static final Molecule m18 = new Molecule("m18", m18Adjacencies, Direction.Right, Direction.DownRight, Direction.DownLeft, Direction.Left);
  public static final Molecule m19 = new Molecule("m19", m19Adjacencies, Direction.Right, Direction.Right, Direction.DownRight, Direction.Back, Direction.UpRight);
  public static final Molecule m20 = new Molecule("m20", m20Adjacencies, M20_BEAD_IDS, Orientation.Left, Direction.Right, Direction.UpRight, Direction.DownRight, Direction.DownLeft, Direction.UpLeft, Direction.Right);
  public static final Molecule m21 = new Molecule("m21", m21Adjacencies, Direction.DownRight, Direction.UpRight, Direction.DownRight, Direction.UpRight, Direction.Left, Direction.Left, Direction.Back, Direction.Back, Direction.Back, Direction.Left);
  public static final Molecule m22 = new Molecule("m22", m22Adjacencies, M22_BEAD_IDS, Orientation.Left, Direction.DownRight, Direction.UpRight, Direction.UpRight, Direction.DownRight, Direction.Left, Direction.Left);

  public static final Molecule dimer = new Molecule("dimer", dimerAdjacencies, DIMER_BEAD_IDS, Orientation.AChiral, Direction.Right);

  public static final Molecule hole = new Molecule("hole", holeAdjacencies, new int[] {0}, Orientation.Circular);

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

  public boolean isChiral() {
    return orientation == Orientation.Left || orientation == Orientation.Right;
  }

  public Direction getRotation() {
    return rotation;
  }

  public int getDistinctRotationCount() {
    switch (orientation) {
      case Left:
      case Right:
      case AChiral:
        return 6;
      case Symmetric:
        return 3;
      case Circular:
        return 1;
      default:
        throw new IllegalArgumentException("Unknown orientation: " + orientation);
    }
  }

  public List<Direction> getDirections() {
    return Arrays.asList(buildInstructions);
  }

  int[] getBeadIds() {
    return this.beadIds;
  }

  @SuppressWarnings({"ForLoopReplaceableByForEach"})
  public Node getBeadNode(Node startingNode, int beadId) {
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
      int backIndex = directionIndex - 2;
      while (nextDirection == Direction.Back) {
        Direction backDirection = buildInstructions[backIndex--].opposite();
        next = next.get(backDirection);
        nextDirection = buildInstructions[directionIndex++];
      }
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
    Node next = startingNode;
    for (int i = 0; i < buildInstructions.length; ) {
      Direction direction = buildInstructions[i++];
      int backIndex = i - 2;
      while (direction == Direction.Back) {
        Direction backDirection = buildInstructions[backIndex--].opposite();
        next = next.get(backDirection);
        direction = buildInstructions[i++];
      }
      next = next.get(direction);
      if (next == null) {
        return null;
      }
      usedNodeIds.add(next.getId());
    }
    return usedNodeIds;
  }

  public List<BondKey> getBondKeys(Node startingNode) {
    List<BondKey> bondKeys = new ArrayList<>();
    Node next = startingNode;
    for (int i = 0; i < buildInstructions.length; ) {
      Direction direction = buildInstructions[i++];
      int backIndex = i - 2;
      while (direction == Direction.Back) {
        Direction backDirection = buildInstructions[backIndex--].opposite();
        next = next.get(backDirection);
        direction = buildInstructions[i++];
      }
      Node prev = next;
      next = next.get(direction);
      if (next == null) {
        return null;
      }
      bondKeys.add(new BondKey(prev.getId(), direction, next.getId()));
    }
    return bondKeys;
  }

  public boolean equals(Object object) {
    if (!(object instanceof Molecule)) {
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
    return Objects.hash(parts.toArray(new Object[0]));
  }

  public static Molecule fromNumber(int index) {
    if (index < 1 || index > allMolecules.length) {
      throw new IllegalArgumentException("Invalid molecule number: " + index);
    }
    return allMolecules[index - 1];
  }

  public List<String> getAdjacencyOrder() {
    if (size() == 2) {
      return DIMER_ADJACENCY_ORDER;
    }
    return ADJACENCY_ORDER;
  }

  public List<String> getHighAdjacencyOrder() {
    return HIGH_ADJACENCY_ORDER;
  }

  public List<String> getInterAdjacencyOrder() {
    return INTER_ADJACENCY_ORDER;
  }

  static String buildAdjacencyName(int beadId1, int beadId2) {
    if (beadId1 > beadId2) {
      int temp = beadId1;
      beadId1 = beadId2;
      beadId2 = temp;
    }
    return beadId1 + "-" + beadId2;
  }

  public void subtractInternalAdjacencies(Map<String, Integer> adjacencyCountMap, boolean isHighMolecule) {
    List<String> adjacencyOrder = isHighMolecule ? getHighAdjacencyOrder() : getAdjacencyOrder();
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
