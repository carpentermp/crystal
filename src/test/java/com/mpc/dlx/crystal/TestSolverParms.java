package com.mpc.dlx.crystal;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestSolverParms {

  @Test
  public void testSolverParms() {
    SolverParms parms = new SolverParms("5", "inputDir");
    assertEquals(Molecule.m05, parms.getMolecule());
    assertEquals("inputDir", parms.getInputDir());
    assertNull(parms.getOutputDir());
    assertEquals(0, parms.getStartingCrystal());
    assertEquals(0, parms.getEndingCrystal());
    assertEquals(0, parms.getExtraHoles());
    assertTrue(parms.isDedup());
    assertFalse(parms.isDoGZip());
    assertEquals(SolverParms.NEVER, parms.getQuitTime());
    assertEquals(SolverParms.INFINITE, parms.getMaxSolutionCount());
    long now = System.currentTimeMillis();
    parms.molecule(Molecule.m01)
      .outputDir("outputDir")
      .startingCrystal(10)
      .endingCrystal(20)
      .extraHoles(5)
      .dedup(false)
      .doGZip(true)
      .quitTime(now)
      .maxSolutionCount(1000);
    SolverParms parms2 = new SolverParms(parms);
    assertEquals(Molecule.m01, parms2.getMolecule());
    assertEquals("inputDir", parms2.getInputDir());
    assertEquals("outputDir", parms2.getOutputDir());
    assertEquals(10, parms2.getStartingCrystal());
    assertEquals(20, parms2.getEndingCrystal());
    assertEquals(5, parms2.getExtraHoles());
    assertFalse(parms2.isDedup());
    assertTrue(parms2.isDoGZip());
    assertEquals(now, parms2.getQuitTime());
    assertEquals(1000, parms2.getMaxSolutionCount());
  }

  @Test
  public void testSolverParmsWithArgs() {
    SolverParms parms = new SolverParms("-o outputDir -s 10 -e 20 -d -h 5 -g -q 12h -m 1000 1 inputDir".split(" "));
    assertEquals(Molecule.m01, parms.getMolecule());
    assertEquals("inputDir", parms.getInputDir());
    assertEquals("outputDir", parms.getOutputDir());
    assertEquals(10, parms.getStartingCrystal());
    assertEquals(20, parms.getEndingCrystal());
    assertEquals(5, parms.getExtraHoles());
    assertFalse(parms.isDedup());
    assertTrue(parms.isDoGZip());
    assertEquals(1000, parms.getMaxSolutionCount());
    long nowPlus12Hours = System.currentTimeMillis() + 12 * SolverParms.HOUR;
    long quitTime = parms.getQuitTime();
    long diff = Math.abs(quitTime - nowPlus12Hours);
    // assert that it got within 3 seconds of calculating the right quit time (leave slush because of execution time)
    assertTrue(diff < 3000);
  }

  @Test
  public void testInvalid() {
    try {
      new SolverParms();
      fail("Should have failed with no parameters");
    }
    catch (IllegalArgumentException e) {
      // expected
    }
  }

}
