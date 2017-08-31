package com.mpc.dlx.crystal;

import au.id.bjf.dlx.DLX;
import au.id.bjf.dlx.data.ColumnObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "squid:S106", "squid:HiddenFieldCheck"})
public class CrystalSolver {

  private final Crystal crystal;
  final String[] columnNames;
  final List<Molecule> molecules;
  final List<Row> rows;
  final byte[][] matrix;

  public CrystalSolver(Molecule molecule, Crystal crystal) {
    this.crystal = crystal;
    columnNames = crystal.getSortedNodeNames();
    molecules = buildMolecules(molecule);
    rows = buildRows(molecules);
    matrix = buildMatrix(rows);
  }

  private List<Molecule> buildMolecules(Molecule molecule) {
    List<Molecule> moleculeVariants = new ArrayList<>();
    moleculeVariants.add(molecule);
    Molecule l = molecule;
    Molecule r = null;
    if (molecule.getOrientation() != Orientation.Symmetric && molecule.getOrientation() != Orientation.AChiral) {
      r = molecule.mirror(Direction.Right);
      moleculeVariants.add(r);
    }
    int rotations = molecule.getOrientation() == Orientation.Symmetric ? 2 : 5;
    for (int i = 0; i < rotations; i++) {
      l = l.rotate();
      moleculeVariants.add(l);
      if (r != null) {
        r = r.rotate();
        moleculeVariants.add(r);
      }
    }
    return moleculeVariants;
  }

  private List<Row> buildRows(List<Molecule> molecules) {
    List<Row> rows = new ArrayList<>();
    for (String columnName : columnNames) {
      for (Molecule m : molecules) {
        safeAddRow(rows, buildRow(Integer.parseInt(columnName), m));
      }
    }
    return rows;
  }

  private byte[][] buildMatrix(List<Row> rows) {
    byte[][] matrix = new byte[rows.size()][];
    for (int i = 0; i < matrix.length; i++) {
      matrix[i] = rows.get(i).getBytes(columnNames);
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
    if (usedIds.size() != 5) {
      throw new IllegalArgumentException("Bogus used ids set!");
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
    new CrystalSolver(Molecule.m05, new Crystal(Utils.getResourceFilename("neighbors.txt"))).solve();
  }

}
