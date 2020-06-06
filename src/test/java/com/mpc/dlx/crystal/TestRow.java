package com.mpc.dlx.crystal;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class TestRow {

  private Molecule molecule;
  private Crystal c554;
  private Crystal c59;
  private Set<Integer> usedIds;
  private Row row;

  @Before
  public void setUp() {
    molecule = new Molecule("M00", new int[] {}, Direction.DownRight, Direction.Right, Direction.Right, Direction.UpLeft, Direction.DownLeft);
    c554 = new Crystal(Utils.getResourceFilename("554"), molecule.size());
    c59 = new Crystal(Utils.getResourceFilename("59"), molecule.size());
    usedIds = molecule.getUsedNodeIds(c554.getNode(2423));
    row = new Row(2423, molecule, usedIds);
  }

  @Test
  public void testRow() {
    Molecule moleculeRtn = row.getMolecules().iterator().next();
    assertEquals(2423, row.getNodeId(moleculeRtn));
    assertEquals(molecule, moleculeRtn);
    assertTrue(usedIds.contains(2423));
    assertTrue(usedIds.contains(864));
    assertTrue(usedIds.contains(944));
    assertTrue(usedIds.contains(2503));
    assertTrue(usedIds.contains(904));
    assertFalse(row.isHole());
  }

  @Test
  public void testHoleRow() {
    Row holeRow1 = new Row(2421, 0);
    Row holeRow2 = new Row(2421, 1);
    assertTrue(holeRow1.isHole());
    assertTrue(holeRow2.isHole());
    String[] columnNames = CrystalSolver.buildColumnNames(c59, 0, null);
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
    String[] columnNames = CrystalSolver.buildColumnNames(c554, 0, null);
    byte[] bytes = row.getBytes(columnNames);
    assertEquals(1, bytes[18]);
    assertEquals(1, bytes[27]);
    assertEquals(1, bytes[57]);
    assertEquals(1, bytes[62]);
    assertEquals(1, bytes[67]);
    assertEquals(0, bytes[1]);
    assertEquals(0, bytes[2]);
    assertEquals(0, bytes[33]);
    assertEquals(0, bytes[55]);
  }

}
