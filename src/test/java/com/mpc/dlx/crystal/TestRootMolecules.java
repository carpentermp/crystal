package com.mpc.dlx.crystal;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TestRootMolecules {

  private static RootMolecules RM1 = new RootMolecules(Molecule.m05, null, true);
  private static RootMolecules RM2 = new RootMolecules(Molecule.m12, null, false);
  private static RootMolecules RM3 = new RootMolecules(Molecule.m09, Molecule.m10, false);
  private static RootMolecules RM4 = new RootMolecules(Molecule.m10.enantiomer(), Molecule.m09, false);
  private static RootMolecules RM5 = new RootMolecules(Molecule.m09, Molecule.m10, true);
  private static RootMolecules RM6 = new RootMolecules(Molecule.dimer, null, false);

  @Test
  public void testName() {
    assertEquals("m05", RM1.getName());
    assertEquals("m12", RM2.getName());
    assertEquals("m09L_m10L", RM3.getName());
    assertEquals("m09L_m10R", RM4.getName());
    assertEquals("m09_m10", RM5.getName());
    assertEquals("dimer", RM6.getName());
  }

  @Test
  public void testInvalid() {
    shouldFail(() -> new RootMolecules(Molecule.m05, Molecule.m12, true));
    shouldFail(() -> new RootMolecules(Molecule.m12, null, true));
    shouldFail(() -> new RootMolecules(Molecule.m12, Molecule.dimer, true));
    shouldFail(() -> new RootMolecules(null, Molecule.dimer, false));
    shouldFail(() -> new RootMolecules(Molecule.dimer, null, true));
    shouldFail(() -> new RootMolecules(Molecule.m05, Molecule.m05, true));
  }

  @Test
  public void testMisc() {
    assertEquals(Molecule.m05, RM1.getMolecule1());
    assertNull(RM1.getMolecule2());
    assertEquals(Molecule.m09, RM3.getMolecule1());
    assertEquals(Molecule.m10, RM3.getMolecule2());
    assertEquals(5, RM1.moleculeSize());
    assertEquals(2, RM6.moleculeSize());
    assertEquals(1, RM1.moleculeCount());
    assertEquals(2, RM3.moleculeCount());
  }

  @Test
  public void testSameSpecificOrientation() {
    assertFalse(RM1.twoMoleculesHaveSameSpecificOrientation());
    assertFalse(RM2.twoMoleculesHaveSameSpecificOrientation());
    assertTrue(RM3.twoMoleculesHaveSameSpecificOrientation());
    assertFalse(RM4.twoMoleculesHaveSameSpecificOrientation());
    assertFalse(RM5.twoMoleculesHaveSameSpecificOrientation());
    assertFalse(RM6.twoMoleculesHaveSameSpecificOrientation());
  }

  @Test
  public void testTwoWithEnantiomers() {
    assertFalse(RM1.twoMoleculesWithEnantiomers());
    assertFalse(RM2.twoMoleculesWithEnantiomers());
    assertFalse(RM3.twoMoleculesWithEnantiomers());
    assertFalse(RM4.twoMoleculesWithEnantiomers());
    assertTrue(RM5.twoMoleculesWithEnantiomers());
    assertFalse(RM6.twoMoleculesWithEnantiomers());
  }

  @Test
  public void testGetAdjacencyBeadCount() {
    assertEquals(5, RM1.getAdjacencyBeadCount());
    assertEquals(5, RM2.getAdjacencyBeadCount());
    assertEquals(10, RM3.getAdjacencyBeadCount());
    assertEquals(10, RM4.getAdjacencyBeadCount());
    assertEquals(10, RM5.getAdjacencyBeadCount());
    assertEquals(2, RM6.getAdjacencyBeadCount());
  }

  @Test
  public void testGetDistinctBeadCount() {
    assertEquals(10, RM1.getDistinctBeadCount());
    assertEquals(5, RM2.getDistinctBeadCount());
    assertEquals(10, RM3.getDistinctBeadCount());
    assertEquals(10, RM4.getDistinctBeadCount());
    assertEquals(20, RM5.getDistinctBeadCount());
    assertEquals(2, RM6.getDistinctBeadCount());
  }

  @Test
  public void testGetAdjacencyHeader() {
    String tiny = Utils.join(Molecule.dimer.getAdjacencyOrder(), " ");
    String small = Utils.join(Molecule.m01.getAdjacencyOrder(), " ");
    String large = Utils.join(Molecule.m01.getInterAdjacencyOrder(), " ");
    assertEquals(small, RM1.getAdjacencyHeader());
    assertEquals(small, RM2.getAdjacencyHeader());
    assertEquals(large, RM3.getAdjacencyHeader());
    assertEquals(large, RM4.getAdjacencyHeader());
    assertEquals(large, RM5.getAdjacencyHeader());
    assertEquals(tiny, RM6.getAdjacencyHeader());
  }

  @Test
  public void testIsHighMolecule() {
    assertFalse(RM1.isHighMolecule(Molecule.m05));
    assertFalse(RM2.isHighMolecule(Molecule.m05));
    assertFalse(RM3.isHighMolecule(Molecule.m09));
    assertTrue(RM3.isHighMolecule(Molecule.m10));
    assertFalse(RM5.isHighMolecule(Molecule.m09));
    assertFalse(RM5.isHighMolecule(Molecule.m09.enantiomer()));
    assertTrue(RM5.isHighMolecule(Molecule.m10));
    assertTrue(RM5.isHighMolecule(Molecule.m10.enantiomer()));
  }

  @Test
  public void testAdjacencyBeadIdOffset() {
    assertEquals(0, RM1.getAdjacencyBeadIdOffset(Molecule.m05));
    assertEquals(0, RM3.getAdjacencyBeadIdOffset(Molecule.m09));
    assertEquals(5, RM3.getAdjacencyBeadIdOffset(Molecule.m10));
    assertEquals(0, RM4.getAdjacencyBeadIdOffset(Molecule.m09));
    assertEquals(5, RM4.getAdjacencyBeadIdOffset(Molecule.m10));
    assertEquals(0, RM5.getAdjacencyBeadIdOffset(Molecule.m09));
    assertEquals(5, RM5.getAdjacencyBeadIdOffset(Molecule.m10));
  }

  @Test
  public void testAsList() {
    assertList(RM1, Molecule.m05, Molecule.m05.enantiomer());
    assertList(RM2, Molecule.m12);
    assertList(RM3, Molecule.m09, Molecule.m10);
    assertList(RM4, Molecule.m09, Molecule.m10.enantiomer());
    assertList(RM5, Molecule.m09, Molecule.m10, Molecule.m09.enantiomer(), Molecule.m10.enantiomer());
    assertList(RM6, Molecule.dimer);
  }

  @Test
  public void testDistinctBeadIdOffset() {
    assertEquals(0, RM1.getDistinctBeadIdOffset(Molecule.m05));
    assertEquals(5, RM1.getDistinctBeadIdOffset(Molecule.m05.enantiomer()));
    assertEquals(0, RM3.getDistinctBeadIdOffset(Molecule.m09));
    assertEquals(5, RM3.getDistinctBeadIdOffset(Molecule.m10));
    assertEquals(0, RM4.getDistinctBeadIdOffset(Molecule.m09));
    assertEquals(5, RM4.getDistinctBeadIdOffset(Molecule.m10));
    assertEquals(0, RM5.getDistinctBeadIdOffset(Molecule.m09));
    assertEquals(5, RM5.getDistinctBeadIdOffset(Molecule.m10));
    assertEquals(10, RM5.getDistinctBeadIdOffset(Molecule.m09.enantiomer()));
    assertEquals(15, RM5.getDistinctBeadIdOffset(Molecule.m10.enantiomer()));
  }

  private static void assertList(RootMolecules rootMolecules, Molecule... expected) {
    List<Molecule> molecules = rootMolecules.asList();
    assertEquals(expected.length, molecules.size());
    for (int i = 0; i < expected.length; i++) {
      assertEquals(expected[i], molecules.get(i));
    }
  }

  private static void shouldFail(Runnable runnable) {
    try {
      runnable.run();
      fail("Should have failed");
    }
    catch (Exception e) {
      // expected
    }
  }

}

