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
  private Crystal crystal = new Crystal("/Users/carpentermp/Downloads/neighbors.txt");

  @Before
  public void setUp() {
    molecule = new Molecule(Direction.DownRight, Direction.Right, Direction.Right, Direction.UpLeft, Direction.DownLeft);
    mirrored = molecule.mirror(Direction.Left);
  }

  @Test
  public void testMirror() {
    List<Direction> mirroredDirections = mirrored.getDirections();
    assertEquals(Direction.UpRight, mirroredDirections.get(0));
    assertEquals(Direction.Right, mirroredDirections.get(1));
    assertEquals(Direction.Right, mirroredDirections.get(2));
    assertEquals(Direction.DownLeft, mirroredDirections.get(3));
    assertEquals(Direction.UpLeft, mirroredDirections.get(4));
  }

  @Test
  public void testRotate() {
    Molecule rotated = molecule.rotate();
    List<Direction> rotatedDirections = rotated.getDirections();
    assertEquals(Direction.DownLeft, rotatedDirections.get(0));
    assertEquals(Direction.DownRight, rotatedDirections.get(1));
    assertEquals(Direction.DownRight, rotatedDirections.get(2));
    assertEquals(Direction.UpRight, rotatedDirections.get(3));
    assertEquals(Direction.Left, rotatedDirections.get(4));
  }

//  @Test
//  public void testGetUsedNodeIds() {
//    assertUsedNodeIds(molecule.getUsedNodeIds(crystal.getNode(19)), 19, 26, 27, 28, 21);
//    assertUsedNodeIds(molecule.rotate().getUsedNodeIds(crystal.getNode(19)), 19, 25, 33, 40, 34);
//    assertUsedNodeIds(molecule.rotate().rotate().getUsedNodeIds(crystal.getNode(19)), 19, 18, 24, 31, 32);
//    assertUsedNodeIds(molecule.rotate().rotate().rotate().getUsedNodeIds(crystal.getNode(19)), 19, 12, 11, 44, 17);
//    assertUsedNodeIds(molecule.rotate().rotate().rotate().rotate().getUsedNodeIds(crystal.getNode(19)), 19, 13, 8, 4, 7);
//    assertUsedNodeIds(molecule.rotate().rotate().rotate().rotate().rotate().getUsedNodeIds(crystal.getNode(19)), 19, 20, 14, 10, 9);
//
//    assertUsedNodeIds(mirrored.getUsedNodeIds(crystal.getNode(19)), 19, 13, 14, 15, 21);
//    assertUsedNodeIds(mirrored.rotate().getUsedNodeIds(crystal.getNode(19)), 19, 20, 27, 35, 34);
//    assertUsedNodeIds(mirrored.rotate().rotate().getUsedNodeIds(crystal.getNode(19)), 19, 26, 33, 39, 32);
//    assertUsedNodeIds(mirrored.rotate().rotate().rotate().getUsedNodeIds(crystal.getNode(19)), 19, 25, 24, 55, 17);
//    assertUsedNodeIds(mirrored.rotate().rotate().rotate().rotate().getUsedNodeIds(crystal.getNode(19)), 19, 18, 11, 6, 7);
//    assertUsedNodeIds(mirrored.rotate().rotate().rotate().rotate().rotate().getUsedNodeIds(crystal.getNode(19)), 19, 12, 8, 5, 9);
//
//    assertNull(molecule.getUsedNodeIds(crystal.getNode(60)));
//    assertNull(mirrored.getUsedNodeIds(crystal.getNode(23)));
//  }

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
    Molecule m1 = new Molecule(Direction.Left, Direction.DownLeft);
    Molecule m2 = new Molecule(Direction.Left, Direction.DownLeft);
    Molecule m3 = new Molecule(Direction.Left, Direction.DownRight);
    assertEquals(m1, m2);
    assertNotEquals(m1, m3);
    assertFalse(m1.equals(null));
    assertFalse(m1.equals(crystal));
  }

}
