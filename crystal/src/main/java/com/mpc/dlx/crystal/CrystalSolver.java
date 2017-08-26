package com.mpc.dlx.crystal;

import au.id.bjf.dlx.DLX;
import au.id.bjf.dlx.data.ColumnObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public class CrystalSolver {

  public static final int COLUMN_COUNT = 60;

  private final Molecule l1 = new Molecule(Direction.DownRight, Direction.Right, Direction.Right, Direction.UpLeft, Direction.DownLeft);
  private final Molecule l2 = l1.rotate();
  private final Molecule l3 = l2.rotate();
  private final Molecule l4 = l3.rotate();
  private final Molecule l5 = l4.rotate();
  private final Molecule l6 = l5.rotate();

  private final Molecule r1 = l1.mirror(Direction.Left);
  private final Molecule r2 = r1.rotate();
  private final Molecule r3 = r2.rotate();
  private final Molecule r4 = r3.rotate();
  private final Molecule r5 = r4.rotate();
  private final Molecule r6 = r5.rotate();

  private final Crystal crystal = new Crystal();
  final String[] columnNames = new String[COLUMN_COUNT];
  final List<Row> rows;
  final byte[][] matrix;

  public CrystalSolver() {
    for (int i = 0; i < columnNames.length; i++) {
      columnNames[i] = "c" + (i + 1);
    }
    rows = buildRows();
    matrix = buildMatrix(rows);
  }

  private List<Row> buildRows() {
    List<Row> rows = new ArrayList<>();
    for (int i = 1; i <= 60; i++) {
      safeAddRow(rows, buildRow(i, l1));
      safeAddRow(rows, buildRow(i, l2));
      safeAddRow(rows, buildRow(i, l3));
      safeAddRow(rows, buildRow(i, l4));
      safeAddRow(rows, buildRow(i, l5));
      safeAddRow(rows, buildRow(i, l6));
      safeAddRow(rows, buildRow(i, r1));
      safeAddRow(rows, buildRow(i, r2));
      safeAddRow(rows, buildRow(i, r3));
      safeAddRow(rows, buildRow(i, r4));
      safeAddRow(rows, buildRow(i, r5));
      safeAddRow(rows, buildRow(i, r6));
    }
    return rows;
  }

  private byte[][] buildMatrix(List<Row> rows) {
    byte[][] matrix = new byte[rows.size()][];
    for (int i = 0; i < matrix.length; i++) {
      matrix[i] = rows.get(i).getBytes();
    }
    return matrix;
  }

  private void safeAddRow(List<Row> list, Row row) {
    if (row != null) {
      list.add(row);
    }
  }

  private Row buildRow(int nodeId, Molecule molecule) {
    Set<Integer> usedIds = molecule.getUsedNodeIds(crystal.getNode(nodeId));
    if (usedIds == null) {
      return null;
    }
    return new Row(nodeId, molecule, usedIds);
  }

  public void solve() {
    ColumnObject h = DLX.buildSparseMatrix(matrix, columnNames);
    CrystalResultProcessor resultProcessor = new CrystalResultProcessor(rows);
    DLX.solve(h, true, resultProcessor);
    System.out.println("There were " + resultProcessor.getCount() + " results!");
    int[] leftCounts = resultProcessor.getLeftCounts();
    for (int i = 0; i < leftCounts.length; i++) {
      System.out.println("  Solutions where left-side molecules was " + i + ": " + leftCounts[i]);
    }
  }

  public static void main(String[] args) {
    new CrystalSolver().solve();
  }

}
