package com.mpc.dlx.crystal;

import au.id.bjf.dlx.DLX;
import au.id.bjf.dlx.DLXResult;
import au.id.bjf.dlx.DLXResultProcessor;
import au.id.bjf.dlx.data.ColumnObject;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "squid:S106", "squid:HiddenFieldCheck"})
public class CrystalSolver {

  private final Crystal crystal;
  final String[] columnNames;
  final List<Molecule> molecules;
  final List<Row> rows;
  final byte[][] matrix;
  private int count = 0;
  private final Map<String, List<CrystalResult>> resultMap = new HashMap<>();

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
    DLX.solve(h, true, new CrystalResultProcessor());
    System.out.println("There were " + count + " results!");
    for (Map.Entry<String, List<CrystalResult>> entry : resultMap.entrySet()) {
      System.out.println(entry.getKey() + ": " + entry.getValue().size());
    }
  }

  public class CrystalResultProcessor implements DLXResultProcessor {

    public boolean processResult(DLXResult dlxResult) {
      count++;
      CrystalResult result = new CrystalResult(dlxResult, rows);
      String leftRight = String.format("l%1$02dr%2$02d", result.getLeftCount(), result.getRightCount());
      List<CrystalResult> results = resultMap.computeIfAbsent(leftRight, k -> new ArrayList<>());
      results.add(result);
      return true; // keep going
    }

  }
  public static void main(String[] args) {
    new CrystalSolver(Molecule.m05, new Crystal("/Users/merlin/Downloads/textfiles/1372/")).solve();
  }

}
