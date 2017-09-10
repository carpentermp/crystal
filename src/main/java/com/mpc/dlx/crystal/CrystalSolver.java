package com.mpc.dlx.crystal;

import au.id.bjf.dlx.DLX;
import au.id.bjf.dlx.DLXResult;
import au.id.bjf.dlx.DLXResultProcessor;
import au.id.bjf.dlx.data.ColumnObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mpc.dlx.crystal.result.UnitCellResults;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@SuppressWarnings({"WeakerAccess", "squid:S106", "squid:HiddenFieldCheck"})
public class CrystalSolver {

  private static final String HOLES_PREFIX = "h";
  private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

  private final Crystal crystal;
  private final Molecule rootMolecule;
  final String[] columnNames;
  final List<Molecule> molecules;
  final List<Row> rows;
  final byte[][] matrix;
  private final Map<String, Set<CrystalResult>> resultMap = new HashMap<>();
  private final Map<String, Integer> resultDuplicationCounts = new HashMap<>();
  private final boolean deduplicateResults;

  public CrystalSolver(Crystal crystal, Molecule molecule, boolean deduplicateResults) {
    this.crystal = crystal;
    this.rootMolecule = molecule;
    this.deduplicateResults = deduplicateResults;
    this.columnNames = buildColumnNames(crystal);
    this.molecules = buildMolecules(molecule);
    this.rows = buildRows(molecules);
    this.matrix = buildMatrix(rows);
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

  public CrystalSolver solve() {
    CrystalResultProcessor resultProcessor = new CrystalResultProcessor();
    if (matrix.length > 0) {
      ColumnObject h = DLX.buildSparseMatrix(matrix, columnNames);
      DLX.solve(h, true, resultProcessor);
    }
    System.out.println("For " + crystal.getName() + "-" + rootMolecule.getName() + " there were " + resultProcessor.getCount() + " results!");
    for (Map.Entry<String, Set<CrystalResult>> entry : resultMap.entrySet()) {
      System.out.println(entry.getKey() + ": " + entry.getValue().size());
    }
    System.out.println();
    return this;
  }

  public void output(String outputDir) throws IOException {
    if (outputDir == null) {
      return;
    }
    File moleculeDir = Utils.createSubDir(outputDir, rootMolecule.getName());
    UnitCellResults results = new ResultsMapper(rootMolecule, crystal).map(resultMap, resultDuplicationCounts);
    String filename = Utils.addTrailingSlash(moleculeDir.getAbsolutePath()) + rootMolecule.getName() + "_" + crystal.getName() + ".json";
    String json = gson.toJson(results);
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
      writer.write(json);
    }
  }

  public class CrystalResultProcessor implements DLXResultProcessor {

    private int count = 0;

    public boolean processResult(DLXResult dlxResult) {
      CrystalResult result = new CrystalResult(dlxResult, crystal, rootMolecule, rows, deduplicateResults);
      Set<CrystalResult> results = resultMap.computeIfAbsent(result.getBucketName(), k -> new HashSet<>());
      if (deduplicateResults && results.contains(result)) {
        Integer count = resultDuplicationCounts.get(result.toString());
        if (count == null) {
          count = 1;
        }
        resultDuplicationCounts.put(result.toString(), ++count);
      }
      else {
        results.add(result);
        count++;
      }
      return true; // keep going
    }

    public int getCount() {
      return count;
    }

  }

  private static void solveCrystals(String rootInputDir, String rootOutputDir, Molecule molecule, int start, int end, boolean deduplicateResults) throws IOException {
    for (int i = start; i <= end; i++) {
      Crystal crystal;
      try {
        String baseDir = Utils.addTrailingSlash(rootInputDir) + i + "/";
        crystal = new Crystal(baseDir);
        new CrystalSolver(crystal, molecule, deduplicateResults).solve().output(rootOutputDir);
      }
      catch (RuntimeException e) {
        System.out.println("Failure solving crystal c" + i + "-" + molecule.getName() + " because of: " + e.getClass() + ": " + e.getLocalizedMessage());
        if (!(e instanceof IllegalArgumentException)) {
          e.printStackTrace();
        }
      }
    }
  }

  private static void solveCrystal(String rootInputDir, String rootOutputDir, Molecule molecule, int crystalId, boolean deduplicateResults) throws IOException {
    solveCrystals(rootInputDir, rootOutputDir, molecule, crystalId, crystalId, deduplicateResults);
  }

  public static void main(String[] args) throws IOException {
    System.out.println("it worked!");
//    solveCrystals("/Users/merlin/Downloads/textfiles/", "/Users/merlin/Downloads/crystalResults", Molecule.m05, 0, 870, true);
//    solveCrystals("/Users/merlin/Downloads/textfiles/", "/Users/merlin/Downloads/crystalResults", Molecule.m05, 897, 1031, true);
//    solveCrystals("/Users/merlin/Downloads/textfiles/", "/Users/merlin/Downloads/crystalResults", Molecule.m05, 1089, 1206, true);
//    solveCrystals("/Users/merlin/Downloads/textfiles/", "/Users/merlin/Downloads/crystalResults", Molecule.m05, 1376, 1415, true);
//    solveCrystals("/Users/merlin/Downloads/textfiles/", "/Users/merlin/Downloads/crystalResults", Molecule.m05, 1519, 1646, true);
//    solveCrystals("/Users/merlin/Downloads/textfiles/", "/Users/merlin/Downloads/crystalResults", Molecule.m05, 1741, 1832, true);
//    solveCrystals("/Users/merlin/Downloads/textfiles/", "/Users/merlin/Downloads/crystalResults", Molecule.m05, 2002, 2015, true);
//    solveCrystals("/Users/merlin/Downloads/textfiles/", "/Users/merlin/Downloads/crystalResults", Molecule.m05, 0, 500);
//    solveCrystals("/Users/merlin/Downloads/textfiles/", "/Users/merlin/Downloads/crystalResults", Molecule.m05, 501, 561);
//    solveCrystal("/Users/merlin/Downloads/textfiles/", null, Molecule.m05, 1372, false);
  }

//  For c1647-m05 there were 418082 results!
//  l05r08: 67155
//  l01r12: 739
//  l04r09: 36205
//  l08r05: 64707
//  l09r04: 34098
//  l03r10: 14623
//  l02r11: 4257
//  l06r07: 90018
//  l00r13: 64
//  l07r06: 89133
//  l12r01: 532
//  l13r00: 48
//  l11r02: 3443
//  l10r03: 13060

}