package com.mpc.dlx.crystal;

import au.id.bjf.dlx.DLX;
import au.id.bjf.dlx.DLXResult;
import au.id.bjf.dlx.DLXResultProcessor;
import au.id.bjf.dlx.data.ColumnObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@SuppressWarnings({"WeakerAccess", "squid:S106", "squid:HiddenFieldCheck"})
public class CrystalSolver {

  private static final String HOLES_PREFIX = "h";

  private final Molecule rootMolecule;
  private final Crystal crystal;
  final String[] columnNames;
  final List<Molecule> molecules;
  final List<Row> rows;
  final byte[][] matrix;
  private int count = 0;
  private final Map<String, List<CrystalResult>> resultMap = new HashMap<>();

  public CrystalSolver(Molecule molecule, Crystal crystal) {
    rootMolecule = molecule;
    this.crystal = crystal;
    columnNames = buildColumnNames(crystal);
    molecules = buildMolecules(molecule);
    rows = buildRows(molecules);
    matrix = buildMatrix(rows);
  }

  static String[] buildColumnNames(Crystal crystal) {
    List<String> columnNames = crystal.getSortedNodeNames();
    if (crystal.getHoleCount() > 0) {
      for (int i = crystal.getHoleCount() - 1; i >= 0; i--) {
        columnNames.add(0, HOLES_PREFIX + i);
      }
    }
    return columnNames.toArray(new String[columnNames.size()]);
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
      if (columnName.startsWith(HOLES_PREFIX)) {
        continue;
      }
      int nodeId = Integer.parseInt(columnName);
      for (Molecule m : molecules) {
        safeAddRow(rows, buildRow(nodeId, m));
      }
      if (crystal.getHoleCount() > 0) {
        for (int i = 0; i < crystal.getHoleCount(); i++) {
          rows.add(new Row(nodeId, i));
        }
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
    if (usedIds.size() != rootMolecule.size()) {
      // for small unit cells, the molecules wrap around on them themselves
      return null;
//      throw new IllegalArgumentException("Bogus used ids set!");
    }
    return new Row(nodeId, molecule, usedIds);
  }

  public CrystalSolver solve() {
    if (matrix.length > 0) {
      ColumnObject h = DLX.buildSparseMatrix(matrix, columnNames);
      DLX.solve(h, true, new CrystalResultProcessor());
    }
    System.out.println("For " + crystal.getName() + "-" + rootMolecule.getName() + " there were " + count + " results!");
    for (Map.Entry<String, List<CrystalResult>> entry : resultMap.entrySet()) {
      System.out.println(entry.getKey() + ": " + entry.getValue().size());
    }
    System.out.println();
    return this;
  }

  public void output(String outputDir) throws IOException {
    File moleculeDir = Utils.createSubDir(outputDir, rootMolecule.getName());
    for (Map.Entry<String, List<CrystalResult>> entry : resultMap.entrySet()) {
      outputBucket(moleculeDir, entry.getKey(), entry.getValue());
    }
  }

  private void outputBucket(File moleculeDir, String bucketName, List<CrystalResult> results) throws IOException {
    File bucketDir = Utils.createSubDir(moleculeDir.getAbsolutePath(), bucketName);
    int count = 0;
    for (CrystalResult result : results) {
      String json = result.toJson(crystal, rootMolecule);
      String filename = Utils.addTrailingSlash(bucketDir.getAbsolutePath()) + crystal.getName() + "_" + rootMolecule.getName() + "_" + bucketName + "_" + String.format("%04d", count++) + ".json";
      try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
        writer.write(json);
      }
    }
  }

  public class CrystalResultProcessor implements DLXResultProcessor {

    public boolean processResult(DLXResult dlxResult) {
      count++;
      CrystalResult result = new CrystalResult(dlxResult, rows);
      List<CrystalResult> results = resultMap.computeIfAbsent(result.getBucketName(), k -> new ArrayList<>());
      results.add(result);
      return true; // keep going
    }

  }

  private static void solveSeveralCrystals(String rootDir, int start, int end) {
    for (int i = start; i <= end; i++) {
      Crystal crystal = null;
      Molecule m = Molecule.hole;
      try {
        String baseDir = Utils.addTrailingSlash(rootDir) + i + "/";
        crystal = new Crystal(baseDir);
        for (Molecule molecule : Molecule.allMolecules) {
          m = molecule;
          new CrystalSolver(molecule, crystal).solve();
        }
//        m = Molecule.m22;
//        new CrystalSolver(Molecule.m22, crystal).solve();
      }
      catch (RuntimeException e) {
        System.out.println("Failure solving crystal c" + i + "-" + m.getName() + " because of: " + e.getClass() + ": " + e.getLocalizedMessage());
        if (!(e instanceof IllegalArgumentException)) {
          e.printStackTrace();
        }
      }
    }
  }

  private static void solveACrystal(String baseDir) throws IOException {
    Crystal crystal = new Crystal(baseDir);
//    for (Molecule molecule : Molecule.allMolecules) {
//      new CrystalSolver(molecule, crystal).solve().output(baseDir);
//    }
    new CrystalSolver(Molecule.m05, crystal).solve();
//    new CrystalSolver(Molecule.m05, crystal).solve().output(baseDir);
//    new CrystalSolver(Molecule.m22, crystal).solve().output(baseDir);
  }

  public static void main(String[] args) throws IOException {
//    solveACrystal("/Users/merlin/Downloads/textfiles/1372/");
//    solveACrystal("/Users/merlin/Downloads/textfiles/59/");
    solveSeveralCrystals("/Users/merlin/Downloads/textfiles/", 1, 100);
  }

}
