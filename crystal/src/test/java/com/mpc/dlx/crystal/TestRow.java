package com.mpc.dlx.crystal;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TestRow {

  private Molecule molecule;
  private Crystal crystal;
  private Set<Integer> usedIds;
  private Row row;

  @Before
  public void setUp() {
    molecule = new Molecule(Direction.DownRight, Direction.Right, Direction.Right, Direction.UpLeft, Direction.DownLeft);
    crystal = new Crystal(Utils.getResourceFilename("neighbors.txt"));
    usedIds = molecule.getUsedNodeIds(crystal.getNode(2820));
    row = new Row(2820, molecule, usedIds);
  }

  @Test
  public void testRow() throws Exception {
    assertEquals(2820, row.getNodeId());
    assertEquals(molecule, row.getMolecule());
    assertTrue(usedIds.contains(2820));
    assertTrue(usedIds.contains(1261));
    assertTrue(usedIds.contains(1301));
    assertTrue(usedIds.contains(1301));
    assertTrue(usedIds.contains(2622));
  }

  @Test
  public void testBytes() {
    byte[] bytes = row.getBytes(crystal.getSortedNodeNames());
    assertEquals(1, bytes[6]);
    assertEquals(1, bytes[20]);
    assertEquals(1, bytes[22]);
    assertEquals(1, bytes[34]);
    assertEquals(1, bytes[49]);
    assertEquals(0, bytes[1]);
    assertEquals(0, bytes[2]);
    assertEquals(0, bytes[33]);
    assertEquals(0, bytes[55]);
  }

  @Test
  public void testIsThisRow() throws Exception {
    assertTrue(row.isThisRow(usedIds));
    Set<Integer> otherUsedIds = new HashSet<>(usedIds);
    assertTrue(row.isThisRow(otherUsedIds));
    otherUsedIds.add(1);
    assertFalse(row.isThisRow(otherUsedIds));
    otherUsedIds.remove(55);
    assertFalse(row.isThisRow(otherUsedIds));
  }

}
