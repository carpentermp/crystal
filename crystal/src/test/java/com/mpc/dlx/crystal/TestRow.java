package com.mpc.dlx.crystal;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class TestRow {

//  private Molecule molecule;
//  private Set<Integer> usedIds;
//  private Row row;

//  @Before
//  public void setUp() {
//    molecule = new Molecule(Direction.DownRight, Direction.Right, Direction.Right, Direction.UpLeft, Direction.DownLeft);
//    usedIds = molecule.getUsedNodeIds(new Crystal("/Users/carpentermp/Downloads/neighbors.txt").getNode(55));
//    row = new Row(55, molecule, usedIds);
//  }

//  @Test
//  public void testRow() throws Exception {
//    assertEquals(55, row.getNodeId());
//    assertEquals(molecule, row.getMolecule());
//    assertTrue(usedIds.contains(55));
//    assertTrue(usedIds.contains(31));
//    assertTrue(usedIds.contains(32));
//    assertTrue(usedIds.contains(33));
//    assertTrue(usedIds.contains(25));
//  }
//
//  @Test
//  public void testBytes() {
//    byte[] bytes = row.getBytes();
//    assertEquals(1, bytes[55 - 1]);
//    assertEquals(1, bytes[31 - 1]);
//    assertEquals(1, bytes[32 - 1]);
//    assertEquals(1, bytes[33 - 1]);
//    assertEquals(1, bytes[25 - 1]);
//    assertEquals(0, bytes[24 - 1]);
//    assertEquals(0, bytes[34 - 1]);
//    assertEquals(0, bytes[56 - 1]);
//    assertEquals(0, bytes[10 - 1]);
//  }
//
//  @Test
//  public void testIsThisRow() throws Exception {
//    assertTrue(row.isThisRow(usedIds));
//    Set<Integer> otherUsedIds = new HashSet<>(usedIds);
//    assertTrue(row.isThisRow(otherUsedIds));
//    otherUsedIds.add(1);
//    assertFalse(row.isThisRow(otherUsedIds));
//    otherUsedIds.remove(55);
//    assertFalse(row.isThisRow(otherUsedIds));
//  }

}
