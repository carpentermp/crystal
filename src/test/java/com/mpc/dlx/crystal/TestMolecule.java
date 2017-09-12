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
  private Crystal c1372 = new Crystal(Utils.getResourceFilename("1372"));

  @Before
  public void setUp() {
    molecule = Molecule.m05;
    mirrored = molecule.mirror(Direction.Left);
  }

  @Test
  public void testGetters() {
    assertEquals("m05", molecule.getName());
    assertEquals(5, molecule.size());
    assertEquals(Direction.Right, molecule.getRotation());
    assertEquals(Direction.Right, mirrored.getRotation());
  }

  @Test
  public void testMirror() {
    List<Direction> mirroredDirections = mirrored.getDirections();
    assertEquals(Direction.UpRight, mirroredDirections.get(0));
    assertEquals(Direction.DownRight, mirroredDirections.get(1));
    assertEquals(Direction.Right, mirroredDirections.get(2));
    assertEquals(Direction.UpRight, mirroredDirections.get(3));
    assertEquals(molecule.getBeadIds()[0], mirrored.getBeadIds()[0]);
    assertEquals(molecule.getBeadIds()[1], mirrored.getBeadIds()[1]);
    assertEquals(molecule.getBeadIds()[2], mirrored.getBeadIds()[2]);
    assertEquals(molecule.getBeadIds()[3], mirrored.getBeadIds()[3]);
    assertEquals(molecule.getBeadIds()[4], mirrored.getBeadIds()[4]);
  }

  @Test
  public void testRotate() {
    Molecule rotated = molecule.rotate();
    List<Direction> rotatedDirections = rotated.getDirections();
    assertEquals(Direction.DownLeft, rotatedDirections.get(0));
    assertEquals(Direction.Right, rotatedDirections.get(1));
    assertEquals(Direction.DownRight, rotatedDirections.get(2));
    assertEquals(Direction.DownLeft, rotatedDirections.get(3));
    assertEquals(molecule.getBeadIds()[0], rotated.getBeadIds()[0]);
    assertEquals(molecule.getBeadIds()[1], rotated.getBeadIds()[1]);
    assertEquals(molecule.getBeadIds()[2], rotated.getBeadIds()[2]);
    assertEquals(molecule.getBeadIds()[3], rotated.getBeadIds()[3]);
    assertEquals(molecule.getBeadIds()[4], rotated.getBeadIds()[4]);
    assertEquals(Direction.DownRight, rotated.getRotation());
  }

  @Test
  public void testGetUsedNodeIds() {
    Node startingNode = c1372.getNode(2820);
    Set<Integer> usedNodeIds = molecule.getUsedNodeIds(startingNode);
    assertUsedNodeIds(usedNodeIds, 2820, 2582, 1063, 1261, 2622);
    usedNodeIds = Molecule.m19.getUsedNodeIds(startingNode);
    assertUsedNodeIds(usedNodeIds, 2820, 2582, 2622, 1063, 1062);
  }

  @Test
  public void testGetBeadNode() {
    Node startingNode = c1372.getNode(2820);

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

    try {
      Molecule.m19.getBeadNode(startingNode, 6).getId();
      fail("Should have failed getting bead node 6");
    }
    catch (IllegalArgumentException e) {
      // expected
    }
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
  public void testRotation() {
    Molecule r1 = molecule.rotate();
    Molecule r2 = r1.rotate();
    Molecule r3 = r2.rotate();
    Molecule r4 = r3.rotate();
    Molecule r5 = r4.rotate();
    assertEquals(Direction.DownRight, r1.getRotation());
    assertEquals(Direction.DownLeft, r2.getRotation());
    assertEquals(Direction.Left, r3.getRotation());
    assertEquals(Direction.UpLeft, r4.getRotation());
    assertEquals(Direction.UpRight, r5.getRotation());
  }

  @Test
  public void testEquals() {
    Molecule m1 = new Molecule("M00", new int[] {}, Direction.Left, Direction.DownLeft);
    Molecule m2 = new Molecule("M00", new int[] {}, Direction.Left, Direction.DownLeft);
    Molecule m3 = new Molecule("M00", new int[] {}, Direction.Left, Direction.DownRight);
    assertEquals(m1, m2);
    assertNotEquals(m1, m3);
    assertFalse(m1.equals(null));
    assertFalse(m1.equals(c1372));
    assertNotEquals(molecule, mirrored);
  }

  @Test
  public void testHash() {
    Molecule m1 = new Molecule("M00", new int[] {}, Direction.Left, Direction.DownLeft);
    Molecule m2 = new Molecule("M00", new int[] {}, Direction.Left, Direction.DownLeft);
    Molecule m3 = new Molecule("M00", new int[] {}, Direction.Left, Direction.DownRight);
    assertEquals(m1.hashCode(), m2.hashCode());
    assertNotEquals(m1.hashCode(), m3.hashCode());
  }

  @Test
  public void testBuildAdjacencyName() {
    assertEquals("1-1", Molecule.buildAdjacencyName(1, 1));
    assertEquals("1-3", Molecule.buildAdjacencyName(3, 1));
  }

  @Test
  public void testComputeAdjacencyOrder() {
    assertEquals("1-1, 1-2, 1-3, 1-4, 1-5, 2-2, 2-3, 2-4, 2-5, 3-3, 3-4, 3-5, 4-4, 4-5, 5-5",
                 Utils.join(Molecule.m05.getAdjacencyOrder(), ", "));
  }

}
