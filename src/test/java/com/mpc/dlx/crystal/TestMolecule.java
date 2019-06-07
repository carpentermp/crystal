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
  private Molecule dimer;
  private Crystal c554 = new Crystal(Utils.getResourceFilename("554"));

  @Before
  public void setUp() {
    molecule = Molecule.m05;
    mirrored = molecule.mirror(Direction.Left);
    dimer = Molecule.dimer;
  }

  @Test
  public void testGetters() {
    assertEquals("m05", molecule.getName());
    assertEquals(5, molecule.size());
    assertEquals(2, dimer.size());
    assertEquals(Direction.Right, molecule.getRotation());
    assertEquals(Direction.Right, mirrored.getRotation());
    assertEquals(Direction.Right, dimer.getRotation());
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
    Node startingNode = c554.getNode(2423);
    Set<Integer> usedNodeIds = molecule.getUsedNodeIds(startingNode);
    assertUsedNodeIds(usedNodeIds, 864, 944, 2423, 2503, 2463);
    usedNodeIds = Molecule.m19.getUsedNodeIds(startingNode);
    assertUsedNodeIds(usedNodeIds, 944, 2423, 2503, 2463, 943);
  }

  @Test
  public void testDimerUsedNodeIds() {
    Node startingNode = c554.getNode(2423);
    Set<Integer> usedNodeIds = dimer.getUsedNodeIds(startingNode);
    assertUsedNodeIds(usedNodeIds, 2423, 2463);
  }

  @Test
  public void testBack() {
    Node startingNode = c554.getNode(2423);
    Molecule molecule = new Molecule("test", new int[]{1, 2, 3, 4, 5}, Direction.Right, Direction.DownRight, Direction.UpRight, Direction.Back, Direction.Back, Direction.Back, Direction.UpLeft, Direction.Back, Direction.Right);
    Set<Integer> usedNodeIds = molecule.getUsedNodeIds(startingNode);
    assertEquals(5, usedNodeIds.size());
    assertEquals(823, molecule.getBeadNode(startingNode, 5).getId());
  }

  @Test
  public void testGetBeadNode() {
    Node startingNode = c554.getNode(2423);

    assertEquals(2423, Molecule.m22.getBeadNode(startingNode, 1).getId());
    assertEquals(2463, Molecule.m22.getBeadNode(startingNode, 2).getId());
    assertEquals(2503, Molecule.m22.getBeadNode(startingNode, 3).getId());
    assertEquals(864, Molecule.m22.getBeadNode(startingNode, 4).getId());
    assertEquals(903, Molecule.m22.getBeadNode(startingNode, 5).getId());

    assertEquals(2423, Molecule.m19.getBeadNode(startingNode, 1).getId());
    assertEquals(2463, Molecule.m19.getBeadNode(startingNode, 2).getId());
    assertEquals(2503, Molecule.m19.getBeadNode(startingNode, 3).getId());
    assertEquals(944, Molecule.m19.getBeadNode(startingNode, 4).getId());
    assertEquals(943, Molecule.m19.getBeadNode(startingNode, 5).getId());

    try {
      Molecule.m19.getBeadNode(startingNode, 6).getId();
      fail("Should have failed getting bead node 6");
    }
    catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testGetBondKeys() {
    Node startingNode = c554.getNode(2423);
    assertEquals(4, Molecule.m01.getBondKeys(startingNode).size());
    assertEquals(5, Molecule.m02.getBondKeys(startingNode).size());
    assertEquals(4, Molecule.m03.getBondKeys(startingNode).size());
    assertEquals(6, Molecule.m04.getBondKeys(startingNode).size());
    assertEquals(5, Molecule.m05.getBondKeys(startingNode).size());
    assertEquals(5, Molecule.m06.getBondKeys(startingNode).size());
    assertEquals(5, Molecule.m07.getBondKeys(startingNode).size());
    assertEquals(4, Molecule.m08.getBondKeys(startingNode).size());
    assertEquals(4, Molecule.m09.getBondKeys(startingNode).size());
    assertEquals(4, Molecule.m10.getBondKeys(startingNode).size());
    assertEquals(4, Molecule.m11.getBondKeys(startingNode).size());
    assertEquals(4, Molecule.m12.getBondKeys(startingNode).size());
    assertEquals(5, Molecule.m13.getBondKeys(startingNode).size());
    assertEquals(4, Molecule.m14.getBondKeys(startingNode).size());
    assertEquals(4, Molecule.m15.getBondKeys(startingNode).size());
    assertEquals(5, Molecule.m16.getBondKeys(startingNode).size());
    assertEquals(4, Molecule.m17.getBondKeys(startingNode).size());
    assertEquals(4, Molecule.m18.getBondKeys(startingNode).size());
    assertEquals(4, Molecule.m19.getBondKeys(startingNode).size());
    assertEquals(6, Molecule.m20.getBondKeys(startingNode).size());

    List<BondKey> bondKeys = Molecule.m21.getBondKeys(startingNode);
    assertEquals(7, bondKeys.size());
    assertTrue(bondKeys.contains(new BondKey(864, Direction.UpLeft, 2423)));
    assertTrue(bondKeys.contains(new BondKey(864, Direction.UpRight, 2463)));
    assertTrue(bondKeys.contains(new BondKey(864, Direction.Right, 904)));
    assertTrue(bondKeys.contains(new BondKey(904, Direction.UpLeft, 2463)));
    assertTrue(bondKeys.contains(new BondKey(904, Direction.UpRight, 2503)));
    assertTrue(bondKeys.contains(new BondKey(2423, Direction.Right, 2463)));
    assertTrue(bondKeys.contains(new BondKey(2463, Direction.Right, 2503)));
    assertKeysHaveOffsets(bondKeys, c554);

    bondKeys = Molecule.m22.getBondKeys(startingNode);
    assertEquals(6, bondKeys.size());
    assertTrue(bondKeys.contains(new BondKey(864, Direction.UpLeft, 2423)));
    assertTrue(bondKeys.contains(new BondKey(864, Direction.UpRight, 2463)));
    assertTrue(bondKeys.contains(new BondKey(903, Direction.DownLeft, 2463)));
    assertTrue(bondKeys.contains(new BondKey(903, Direction.DownRight, 2503)));
    assertTrue(bondKeys.contains(new BondKey(2423, Direction.Right, 2463)));
    assertTrue(bondKeys.contains(new BondKey(2463, Direction.Right, 2503)));
    assertKeysHaveOffsets(bondKeys, c554);
  }

  private void assertKeysHaveOffsets(List<BondKey> bondKeys, Crystal crystal) {
    for (BondKey key : bondKeys) {
      assertNotNull(crystal.getBondKeyIndex(key));
    }
  }

  @Test
  public void testFromNumber() {
    assertEquals(Molecule.m06, Molecule.fromNumber(6));
    try {
      Molecule.fromNumber(23);
      fail("Should have failed with number 23 since that's more molecules than we have");
    }
    catch (RuntimeException e) {
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
    assertFalse(m1.equals(c554));
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
    assertEquals("1-1, 1-2, 2-2",
                 Utils.join(Molecule.dimer.getAdjacencyOrder(), ", "));
  }

}
