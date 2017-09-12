package com.mpc.dlx.crystal;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TestRow {

  private Molecule molecule;
  private Crystal c1372;
  private Crystal c59;
  private Set<Integer> usedIds;
  private Row row;

  @Before
  public void setUp() {
    molecule = new Molecule("M00", new int[] {}, Direction.DownRight, Direction.Right, Direction.Right, Direction.UpLeft, Direction.DownLeft);
    c1372 = new Crystal(Utils.getResourceFilename("1372"));
    c59 = new Crystal(Utils.getResourceFilename("59"));
    usedIds = molecule.getUsedNodeIds(c1372.getNode(2820));
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
    assertFalse(row.isHole());
  }

  @Test
  public void testHoleRow() throws Exception {
    Row holeRow1 = new Row(2421, 0);
    Row holeRow2 = new Row(2421, 1);
    assertTrue(holeRow1.isHole());
    assertTrue(holeRow2.isHole());
    String[] columnNames = CrystalSolver.buildColumnNames(c59, 0);
    byte[] hole1Bytes = holeRow1.getBytes(columnNames);
    byte[] hole2Bytes = holeRow2.getBytes(columnNames);
    assertEquals(1, hole1Bytes[0]);
    assertEquals(0, hole1Bytes[1]);
    assertEquals(1, hole1Bytes[2]);
    assertEquals(0, hole1Bytes[3]);
    assertEquals(0, hole2Bytes[0]);
    assertEquals(1, hole2Bytes[1]);
    assertEquals(1, hole2Bytes[2]);
    assertEquals(0, hole2Bytes[3]);
  }

  @Test
  public void testBytes() {
    String[] columnNames = CrystalSolver.buildColumnNames(c1372, 0);
    byte[] bytes = row.getBytes(columnNames);
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
