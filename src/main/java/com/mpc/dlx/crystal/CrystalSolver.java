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
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "squid:S106", "squid:HiddenFieldCheck"})
public class CrystalSolver {

  private static final String HOLES_PREFIX = "h";
  private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

  private final Molecule rootMolecule;
  private final Crystal crystal;
  private final String[] columnNames;
  private final List<Molecule> molecules;
  final List<Row> rows;
  private final Map<String, List<Row>> rowKeyToRows;
  final byte[][] matrix;
  private final Map<String, Set<CrystalResult>> resultMap = new HashMap<>();
  private final Map<String, Integer> resultDuplicationCounts = new HashMap<>();
  private boolean deduplicateResults;
  private int extraHoles;
  private boolean doGZip;

  public CrystalSolver(Molecule molecule, Crystal crystal) {
    this.rootMolecule = molecule;
    this.crystal = crystal;
    this.columnNames = buildColumnNames(crystal, extraHoles);
    this.molecules = buildMolecules(molecule);
    this.rows = buildRows(molecules);
    this.rowKeyToRows = buildRowKeyToRows(this.rows);
    this.matrix = buildMatrix(rows);
  }

  public CrystalSolver deduplicateResults(boolean deduplicateResults) {
    this.deduplicateResults = deduplicateResults;
    return this;
  }

  public CrystalSolver extraHoles(int count) {
    this.extraHoles = count;
    return this;
  }

  public CrystalSolver gZip(boolean doGZip) {
    this.doGZip = doGZip;
    return this;
  }

  static String[] buildColumnNames(Crystal crystal, int extraHoles) {
    List<String> columnNames = crystal.getSortedNodeNames();
    if (extraHoles + crystal.getHoleCount() > 0) {
      for (int i = extraHoles + crystal.getHoleCount() - 1; i >= 0; i--) {
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
      if (getHoleCount() > 0) {
        for (int i = 0; i < getHoleCount(); i++) {
          rows.add(new Row(nodeId, i));
        }
      }
    }
    return rows;
  }

  private Map<String, List<Row>> buildRowKeyToRows(List<Row> rows) {
    boolean hadDupRow = false;
    Map<String, List<Row>> rowKeyToRows = new HashMap<>();
    for (Row row : rows) {
      String key = row.getKey();
      List<Row> rowsWithKey = rowKeyToRows.computeIfAbsent(key, k -> new ArrayList<>());
      rowsWithKey.add(row);
      if (rowsWithKey.size() > 1) {
        hadDupRow = true;
      }
    }
    if (hadDupRow) {
      List<Integer> sizes = rowKeyToRows.values()
          .stream()
          .map(List::size)
          .filter(s -> s > 1)
          .collect(Collectors.toList());
      System.out.println("Crystal " + crystal.getName() + " had duplicate rows! " + Utils.join(sizes, "-"));
    }
    return rowKeyToRows;
  }

  private int getHoleCount() {
    return extraHoles + crystal.getHoleCount();
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
    Set<String> rowKeysSeenSoFar = new HashSet<>();
    List<Row> uniqueRows = rows.stream()
        .filter(r -> {
          if (r.isHole()) {
            return true;
          }
          if (rowKeysSeenSoFar.contains(r.getKey())) {
            return false;
          }
          rowKeysSeenSoFar.add(r.getKey());
          return true;
        })
        .collect(Collectors.toList());
    byte[][] matrix = new byte[uniqueRows.size()][];
    for (int i = 0; i < matrix.length; i++) {
      matrix[i] = uniqueRows.get(i).getBytes(columnNames);
    }
    return matrix;
  }

  private void safeAddRow(List<Row> list, Row row) {
    if (row != null) {
      list.add(row);
    }
  }

  private String getFilenamePrefix() {
    return crystal.getName() + "-" + rootMolecule.getName() + (extraHoles > 0 ? "-h" + extraHoles : "");
  }

  public CrystalSolver solve() {
    CrystalResultProcessor resultProcessor = new CrystalResultProcessor();
    if (matrix.length > 0) {
      ColumnObject h = DLX.buildSparseMatrix(matrix, columnNames);
      DLX.solve(h, true, resultProcessor);
    }
    System.out.println("For " + getFilenamePrefix() + " there were " + resultProcessor.getCount() + " results!");
    for (Map.Entry<String, Set<CrystalResult>> entry : resultMap.entrySet()) {
      System.out.println(entry.getKey() + ": " + entry.getValue().size());
//      for (CrystalResult result : entry.getValue()) {
//        System.out.println("Adjacencies: " + result.getAdjacencyCounts());
//      }
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
    String filename = Utils.addTrailingSlash(moleculeDir.getAbsolutePath()) + getFilenamePrefix() + ".json" + (doGZip ? ".gz" : "");
    try (BufferedWriter writer = Utils.getWriter(filename)) {
      gson.toJson(results, writer);
    }
  }

  public class CrystalResultProcessor implements DLXResultProcessor {

    private int count = 0;

    public boolean processResult(DLXResult dlxResult) {
      List<List<Row>> rowSets = convertResultToRowSets(dlxResult);
      rowSets.forEach(rowSet -> {
        CrystalResult result = new CrystalResult(crystal, rootMolecule, rowSet, deduplicateResults);
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
      });
      return true; // keep going
    }

    public int getCount() {
      return count;
    }

  }

  private List<List<Row>> convertResultToRowSets(DLXResult dlxResult) {
    List<List<Row>> matchingRowsInOrder = new ArrayList<>();
    Iterator<List<Object>> it = dlxResult.rows();
    while (it.hasNext()) {
      List<Object> objects = it.next();
      List<Integer> usedIds = objects
          .stream()
          .map(String::valueOf)
          .filter(s -> Character.isDigit(s.charAt(0)))
          .map(Integer::parseInt)
          .collect(Collectors.toList());
      String rowKey;
      Integer holeIndex = null;
      if (usedIds.size() == 1) {
        holeIndex = objects.stream()
            .map(Object::toString)
            .filter(s -> !Character.isDigit(s.charAt(0)))
            .map(s -> Integer.parseInt(s.substring(1)))
            .findAny()
            .orElse(99);
      }
      rowKey = Row.buildKey(usedIds, holeIndex);
      List<Row> rowsWithRowKey = rowKeyToRows.get(rowKey);
      if (rowsWithRowKey == null) {
        System.out.println("Null rows!");
      }
      matchingRowsInOrder.add(rowKeyToRows.get(rowKey));
    }
    return permute(matchingRowsInOrder, 0);
  }

  static <T extends Object> List<List<T>> permute(List<List<T>> matchingRowsInOrder, int i) {
    List<T> matchingRows = matchingRowsInOrder.get(i);
    if (matchingRows == null) {
      System.out.println("null!!!!");
    }
    List<List<T>> rowsSoFar;
    if (i + 1 < matchingRowsInOrder.size()) {
      rowsSoFar = permute(matchingRowsInOrder, i + 1);
      List<List<T>> rtn = new ArrayList<>();
      for (T matchingRow : matchingRows) {
        for (List<T> soFar : rowsSoFar) {
          List<T> newRows = new ArrayList<>();
          newRows.add(matchingRow);
          newRows.addAll(soFar);
          rtn.add(newRows);
        }
      }
      return rtn;
    }
    else {
      return matchingRows
          .stream()
          .map(r -> new ArrayList<>(Collections.singletonList(r)))
          .collect(Collectors.toList());
    }
  }

  private static void solveCrystals(String rootInputDir, String rootOutputDir, Molecule molecule, int start, int end, boolean deduplicateResults, int extraHoles, boolean doGZip) throws IOException {
    for (int i = start; i <= end; i++) {
      Crystal crystal;
      try {
        String baseDir = Utils.addTrailingSlash(rootInputDir) + i + "/";
        crystal = new Crystal(baseDir);
        new CrystalSolver(molecule, crystal)
          .deduplicateResults(deduplicateResults)
          .extraHoles(extraHoles)
          .gZip(doGZip)
          .solve()
          .output(rootOutputDir);
      }
      catch (RuntimeException e) {
        System.out.println("Failure solving crystal c" + i + "-" + molecule.getName() + " because of: " + e.getClass() + ": " + e.getLocalizedMessage());
        if (!(e instanceof IllegalArgumentException)) {
          e.printStackTrace();
        }
      }
    }
  }

  private static void solveCrystal(String rootInputDir, String rootOutputDir, Molecule molecule, int crystalId, boolean deduplicateResults, int extraHoleCount, boolean doGZip) throws IOException {
    solveCrystals(rootInputDir, rootOutputDir, molecule, crystalId, crystalId, deduplicateResults, extraHoleCount, doGZip);
  }

  private static void usage() {
    System.out.println("Usage: java -jar crystal.jar [options] moleculeNumber inputDir");
    System.out.println("  moleculeNumber must be between 1 and 22");
    System.out.println("  inputDir points to parent directory where all crystal information is stored");
    System.out.println("  Options:");
    System.out.println("  -o dir         output directory (no output if not specified)");
    System.out.println("  -s num         starting crystal number (0 if not specified)");
    System.out.println("  -e num         ending crystal number (same as starting number if not specified)");
    System.out.println("  -d             don't deduplicate results");
    System.out.println("  -h num         count of extra holes (must be multiple of molecule size)");
    System.out.println("  -g             gzip output file(s)");
  }

  private static void solveCrystalsWithArgs(String[] args) throws IOException {
    Molecule molecule = null;
    String inputDir = null;
    String outputDir = null;
    int startingCrystal = 0;
    int endingCrystal = 0;
    boolean deduplicateResults = true;
    int extraHoles = 0;
    boolean doGZip = false;
    try {
      for (int i = 0; i < args.length; i++) {
        String arg = args[i];
        switch (arg) {
          case "-o":
            outputDir = args[++i];
            break;
          case "-s":
            startingCrystal = Integer.parseInt(args[++i]);
            if (endingCrystal == 0) {
              endingCrystal = startingCrystal;
            }
            break;
          case "-e":
            endingCrystal = Integer.parseInt(args[++i]);
            break;
          case "-d":
            deduplicateResults = false;
            break;
          case "-h":
            extraHoles = Integer.parseInt(args[++i]);
            break;
          case "-g":
            doGZip = true;
            break;
          default:
            if (molecule == null) {
              molecule = Molecule.fromNumber(Integer.parseInt(arg));
            }
            else {
              inputDir = arg;
            }
            break;
        }
      }
      if (molecule == null) {
        throw new IllegalArgumentException("Molecule must be specified.");
      }
      if (inputDir == null) {
        throw new IllegalArgumentException("Input directory must be specified.");
      }
      if ((extraHoles % molecule.size()) != 0) {
        throw new IllegalArgumentException("Hole count must be multiple of molecule size");
      }
      solveCrystals(inputDir, outputDir, molecule, startingCrystal, endingCrystal, deduplicateResults, extraHoles, doGZip);
    }
    catch (RuntimeException e) {
      System.out.println("\nError: " + e.getMessage() + "\n");
      usage();
    }
  }

  public static void main(String[] args) throws IOException {
    solveCrystalsWithArgs(args);
//    solveCrystals("/Users/merlin/Downloads/textfiles/", "/Users/merlin/Downloads/crystalResults", Molecule.m05, 0, 390, true, 0, true);
//    solveCrystals("/Users/merlin/Downloads/textfiles/", null, Molecule.m05, 390, 500, true, 0, false);
//    solveCrystals("/Users/merlin/Downloads/textfiles/", "/Users/merlin/Downloads/crystalResults", Molecule.m05, 0, 870, true, 0);
//    solveCrystals("/Users/merlin/Downloads/textfiles/", "/Users/merlin/Downloads/crystalResults", Molecule.m05, 897, 1031, true, 0);
//    solveCrystals("/Users/merlin/Downloads/textfiles/", "/Users/merlin/Downloads/crystalResults", Molecule.m05, 1089, 1206, true, 0);
//    solveCrystals("/Users/merlin/Downloads/textfiles/", "/Users/merlin/Downloads/crystalResults", Molecule.m05, 1376, 1415, true, 0);
//    solveCrystals("/Users/merlin/Downloads/textfiles/", "/Users/merlin/Downloads/crystalResults", Molecule.m05, 1519, 1646, true, 0);
//    solveCrystals("/Users/merlin/Downloads/textfiles/", "/Users/merlin/Downloads/crystalResults", Molecule.m05, 1741, 1832, true, 0);
//    solveCrystals("/Users/merlin/Downloads/textfiles/", "/Users/merlin/Downloads/crystalResults", Molecule.m05, 2002, 2015, true, 0);
//    solveCrystals("/Users/merlin/Downloads/textfiles/", "/Users/merlin/Downloads/crystalResults", Molecule.m05, 0, 500, true, 0);
//    solveCrystals("/Users/merlin/Downloads/textfiles/", "/Users/merlin/Downloads/crystalResults", Molecule.m05, 501, 561, true, 0);
//    solveCrystal("/Users/merlin/Downloads/textfiles/", null, Molecule.m05, 1372, false, 0, false);
//    solveCrystal("/Users/merlin/Downloads/textfiles/", null, Molecule.m05, 29, true, 5);
//    solveCrystal("/Users/merlin/Downloads/textfiles/", null, Molecule.m05, 29, true, 0);
//    solveCrystal("/Users/merlin/Downloads/textfiles/", null, Molecule.m12, 0, true, 0, false);
//    solveCrystal("/Users/merlin/Downloads/textfiles/", "/Users/merlin/Downloads/crystalResults", Molecule.m12, 10, true, 0, false);
  }

}
