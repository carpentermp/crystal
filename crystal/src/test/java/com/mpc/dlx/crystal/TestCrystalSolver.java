package com.mpc.dlx.crystal;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class TestCrystalSolver {

  private CrystalSolver solver;

  @Before
  public void setUp() {
    solver = new CrystalSolver(Molecule.m05, new Crystal(Utils.getResourceDirectory("neighbors.txt")));
  }

  @Test
  public void testCrystalSolver() {
    System.out.println("There are " + solver.matrix.length + " rows.");
    for (byte[] row : solver.matrix) {
      assertEquals(5, countBits(row));
    }
  }

  @Test
  public void testRows() throws Exception {
    List<Row> rows = solver.rows;
    for (int i = 0; i < rows.size() - 1; i++) {
      Row row = rows.get(i);
      for (int j = i + 1; j < rows.size(); j++) {
        Row otherRow = rows.get(j);
        assertFalse(row.isThisRow(otherRow.getUsedIds()));
      }
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

}
