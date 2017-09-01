package com.mpc.dlx.crystal;

import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@SuppressWarnings({"ObjectEqualsNull", "EqualsBetweenInconvertibleTypes"})
public class TestMolecule {

  private Molecule molecule;
  private Molecule mirrored;
  private Crystal crystal = new Crystal(Utils.getResourceDirectory("neighbors.txt"));

  @Before
  public void setUp() {
    molecule = Molecule.m05;
    mirrored = molecule.mirror(Direction.Left);
  }

  @Test
  public void testMirror() {
    List<Direction> mirroredDirections = mirrored.getDirections();
    assertEquals(Direction.UpRight, mirroredDirections.get(0));
    assertEquals(Direction.DownRight, mirroredDirections.get(1));
    assertEquals(Direction.Right, mirroredDirections.get(2));
    assertEquals(Direction.UpRight, mirroredDirections.get(3));
  }

  @Test
  public void testRotate() {
    Molecule rotated = molecule.rotate();
    List<Direction> rotatedDirections = rotated.getDirections();
    assertEquals(Direction.DownLeft, rotatedDirections.get(0));
    assertEquals(Direction.Right, rotatedDirections.get(1));
    assertEquals(Direction.DownRight, rotatedDirections.get(2));
    assertEquals(Direction.DownLeft, rotatedDirections.get(3));
  }

  @Test
  public void testGetUsedNodeIds() {
    Node startingNode = crystal.getNode(2820);
    Set<Integer> usedNodeIds = molecule.getUsedNodeIds(startingNode);
    assertUsedNodeIds(usedNodeIds, 2820, 2582, 1063, 1261, 2622);
    usedNodeIds = Molecule.m19.getUsedNodeIds(startingNode);
    assertUsedNodeIds(usedNodeIds, 2820, 2582, 2622, 1063, 1062);
  }

  @Test
  public void testGetBeadNode() {
    Node startingNode = crystal.getNode(2820);

    assertEquals(2820, Molecule.m22.getBeadNode(startingNode, 1).getId());
    assertEquals(2582, Molecule.m22.getBeadNode(startingNode, 2).getId());
    assertEquals(2622, Molecule.m22.getBeadNode(startingNode, 3).getId());
    assertEquals(1261, Molecule.m22.getBeadNode(startingNode, 4).getId());
    assertEquals(1022, Molecule.m22.getBeadNode(startingNode, 5).getId());

    assertEquals(2820, Molecule.m19.getBeadNode(startingNode, 1).getId());
    assertEquals(2582, Molecule.m19.getBeadNode(startingNode, 2).getId());
    assertEquals(2622, Molecule.m19.getBeadNode(startingNode, 3).getId());
    assertEquals(1063, Molecule.m19.getBeadNode(startingNode, 4).getId());
    assertEquals(1062, Molecule.m19.getBeadNode(startingNode, 5).getId());
  }

  @Test
  public void testOrientation() {
    assertEquals(Orientation.Left, molecule.getOrientation());
    assertEquals(Orientation.Right, mirrored.getOrientation());
  }

  private void assertUsedNodeIds(Set<Integer> usedIds, int... expectedIds) {
    assertEquals(expectedIds.length, usedIds.size());
    for (int expectedId : expectedIds) {
      assertTrue(usedIds.contains(expectedId));
    }
  }

  @Test
  public void testEquals() {
    Molecule m1 = new Molecule("M00", Direction.Left, Direction.DownLeft);
    Molecule m2 = new Molecule("M00", Direction.Left, Direction.DownLeft);
    Molecule m3 = new Molecule("M00", Direction.Left, Direction.DownRight);
    assertEquals(m1, m2);
    assertNotEquals(m1, m3);
    assertFalse(m1.equals(null));
    assertFalse(m1.equals(crystal));
  }

  @Test
  public void testHash() {
    Molecule m1 = new Molecule("M00", Direction.Left, Direction.DownLeft);
    Molecule m2 = new Molecule("M00", Direction.Left, Direction.DownLeft);
    Molecule m3 = new Molecule("M00", Direction.Left, Direction.DownRight);
    assertEquals(m1.hashCode(), m2.hashCode());
    assertNotEquals(m1.hashCode(), m3.hashCode());
  }

}
