package com.mpc.dlx.crystal;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestCrystalResult {

  @Test
  public void testBuildAdjacencyName() {
    assertEquals("1-1", CrystalResult.buildAdjacencyName(1, 1));
    assertEquals("1-3", CrystalResult.buildAdjacencyName(3, 1));
  }

  @Test
  public void testComputeAdjacencyOrder() {
    assertEquals("1-1, 1-2, 1-3, 1-4, 1-5, 2-2, 2-3, 2-4, 2-5, 3-3, 3-4, 3-5, 4-4, 4-5, 5-5",
        Utils.join(CrystalResult.computeAdjacencyOrder(Molecule.m05), ", "));
  }

}
