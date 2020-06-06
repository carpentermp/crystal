package com.mpc.dlx.crystal;

import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TestSymmetry {

  @Test
  public void test166() throws Exception {
    File d166 = new File(Utils.getResourceFilename("166"));
    File symmetryFile = new File(d166 + "/symmetry/pgg.txt");
    Symmetry symmetry = new Symmetry(symmetryFile);
    assertEquals("pgg", symmetry.getName());
    assertEquals(4, symmetry.getRotationalSymmetry());
    assertEquals(0, symmetry.getRequiredHoles().size());
    assertTrue(symmetry.hasPlacements(0));
    assertFalse(symmetry.hasPlacements(903));
    Map<Molecule, Integer> placements = symmetry.getPlacements(0, Molecule.m10);
    Set<Integer> holePlacements = new HashSet<>(Arrays.asList(0, 903, 2502, 2584));
    assertEquals(holePlacements, new HashSet<>(placements.values()));
    assertEquals(holePlacements, symmetry.getHolePlacements(0));
  }

  @Test
  public void test150() throws Exception {
    File d150 = new File(Utils.getResourceFilename("150"));
    File symmetryFile = new File(d150 + "/symmetry/p3.txt");
    Symmetry symmetry = new Symmetry(symmetryFile);
    assertEquals("p3", symmetry.getName());
    assertEquals(3, symmetry.getRotationalSymmetry());
    assertEquals(1, symmetry.getRequiredHoles().size());
    assertEquals(0, (int) symmetry.getRequiredHoles().iterator().next());
    assertFalse(symmetry.hasPlacements(0));
    assertTrue(symmetry.hasPlacements(2504));
    Map<Molecule, Integer> placements = symmetry.getPlacements(2504, Molecule.m10);
    Set<Integer> holePlacements = new HashSet<>(Arrays.asList(2504, 945, 2544));
    assertEquals(holePlacements, new HashSet<>(placements.values()));
    assertEquals(holePlacements, symmetry.getHolePlacements(2504));
  }

}
