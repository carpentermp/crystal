package com.mpc.dlx.crystal;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TestCrystalSolver {

  private CrystalSolver solver;

  @Before
  public void setUp() {
    solver = new CrystalSolver(Molecule.m05, new Crystal(Utils.getResourceFilename("1372")));
  }

  @Test
  public void testCrystalSolver() {
    System.out.println("There are " + solver.matrix.length + " rows.");
    for (byte[] row : solver.matrix) {
      assertEquals(5, countBits(row));
    }
  }

  private int countBits(byte[] row) {
    int bits = 0;
    for (byte aRow : row) {
      if (aRow != 0) {
        bits++;
      }
    }
    return bits;
  }

  @Test
  public void testPermute() {
    List<List<String>> resultsInOrder = new ArrayList<>();
    List<String> step1 = Collections.singletonList("a");
    List<String> step2 = Arrays.asList("b", "c", "d", "e", "x");
    List<String> step3 = Arrays.asList("f", "g");
    List<String> step4 = Collections.singletonList("h");
    resultsInOrder.add(step1);
    resultsInOrder.add(step2);
    resultsInOrder.add(step3);
    resultsInOrder.add(step4);
    List<List<String>> permuted = CrystalSolver.permute(resultsInOrder, 0);
    assertEquals(10, permuted.size());
    assertEquals("a-b-f-h", Utils.join(permuted.get(0), "-"));
    assertEquals("a-b-g-h", Utils.join(permuted.get(1), "-"));
    assertEquals("a-c-f-h", Utils.join(permuted.get(2), "-"));
    assertEquals("a-c-g-h", Utils.join(permuted.get(3), "-"));
    assertEquals("a-d-f-h", Utils.join(permuted.get(4), "-"));
    assertEquals("a-d-g-h", Utils.join(permuted.get(5), "-"));
    assertEquals("a-e-f-h", Utils.join(permuted.get(6), "-"));
    assertEquals("a-e-g-h", Utils.join(permuted.get(7), "-"));
    assertEquals("a-x-f-h", Utils.join(permuted.get(8), "-"));
    assertEquals("a-x-g-h", Utils.join(permuted.get(9), "-"));
  }

}
