package com.mpc.dlx;

import au.id.bjf.dlx.DLX;
import au.id.bjf.dlx.DLXResult;
import au.id.bjf.dlx.DLXResultProcessor;
import au.id.bjf.dlx.data.ColumnObject;

@SuppressWarnings("WeakerAccess")
public class SpanishSquare {

  private static final String[] COLUMN_NAMES = new String[] {
      "Some # in column (1,1)",
      "Some # in column (1,2)",
      "Some # in column (2,1)",
      "Some # in column (2,2)",
      "#1 must appear in row 1",
      "#1 must appear in row 2",
      "#2 must appear in row 1",
      "#2 must appear in row 2",
      "#1 must appear in col 1",
      "#1 must appear in col 2",
      "#2 must appear in col 1",
      "#2 must appear in col 2"
  };

  private static final byte[][] ROWS = new byte[][] {
      {1, 0, 0, 0,   1, 0, 0, 0,   1, 0, 0, 0},
      {1, 0, 0, 0,   0, 0, 1, 0,   0, 0, 1, 0},
      {0, 1, 0, 0,   0, 1, 0, 0,   1, 0, 0, 0},
      {0, 1, 0, 0,   0, 0, 0, 1,   0, 0, 1, 0},
      {0, 0, 1, 0,   1, 0, 0, 0,   0, 1, 0, 0},
      {0, 0, 1, 0,   0, 0, 1, 0,   0, 0, 0, 1},
      {0, 0, 0, 1,   0, 1, 0, 0,   0, 1, 0, 0},
      {0, 0, 0, 1,   0, 0, 0, 1,   0, 0, 0, 1}
  };

  public void solve() {
    ColumnObject h = DLX.buildSparseMatrix(ROWS, COLUMN_NAMES);
    DLX.solve(h, true, new SpanishSquareResultProcessor());
  }

  private class SpanishSquareResultProcessor implements DLXResultProcessor {

    public boolean processResult(DLXResult result) {
      // todo
      return true;
    }
  }

  public static void main(String[] args) {
    new SpanishSquare().solve();
  }

}
