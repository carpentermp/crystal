package com.mpc.dlx.crystal;

import au.id.bjf.dlx.DLX;
import au.id.bjf.dlx.DLXResult;
import au.id.bjf.dlx.DLXResultProcessor;
import au.id.bjf.dlx.data.ColumnObject;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "squid:S106", "squid:HiddenFieldCheck", "unused", "FieldCanBeLocal"})
public class CrystalSolver {

  private static final String SYMMETRIES_DIR = "/symmetry";
  private static final String HOLES_PREFIX = "h";

  private final Molecule rootMolecule;
  private final Molecule rootMolecule2;
  private final Crystal crystal;
  private final int extraHoles;
  private final String[] columnNames;
  private final List<Molecule> molecules;
  final List<Row> rows;
  private final Map<String, List<Row>> rowKeyToRows;
  final byte[][] matrix;
  private final CrystalResults results;
  private final long quitTime;
  private final long maxSolutionCount;
  private final Symmetry symmetry;

  public CrystalSolver(SolverParms parms, Crystal crystal) {
    this(parms, crystal, null);
  }

  public CrystalSolver(SolverParms parms, Crystal crystal, Symmetry symmetry) {
    this.rootMolecule = parms.getMolecule();
    this.rootMolecule2 = parms.getMolecule2();
    this.crystal = crystal;
    this.symmetry = symmetry;
    this.extraHoles = parms.getExtraHoles();
    this.columnNames = buildColumnNames(crystal, extraHoles, symmetry);
    this.molecules = buildMolecules(rootMolecule, rootMolecule2);
    this.rows = buildRows(molecules);
    this.rowKeyToRows = buildRowKeyToRows(this.rows);
    this.matrix = buildMatrix(rows);
    this.results = new CrystalResults(rootMolecule, rootMolecule2, crystal, extraHoles, parms.getOutputDir(), parms.isDedup(), parms.isDoGZip());
    this.quitTime = parms.getQuitTime();
    this.maxSolutionCount = parms.getMaxSolutionCount();
  }

  static String[] buildColumnNames(Crystal crystal, int extraHoles, Symmetry symmetry) {
    List<String> columnNames = crystal.getSortedNodeNames();
    int holeCount = extraHoles + crystal.getHoleCount();
    int holeColumnCount;
    if (symmetry != null) {
      if (holeCount % symmetry.getRotationalSymmetry() != 0) {
        throw new NoSolutionsException("Wrong number of extra holes for this symmetry. Extra holes must be a multiple of rotational symmetry=" + symmetry.getRotationalSymmetry());
      }
      holeColumnCount = holeCount / symmetry.getRotationalSymmetry();
    }
    else {
      holeColumnCount = holeCount;
    }
    // put hole columns at the beginning, so insert them reverse order at position 0
    for (int i = holeColumnCount - 1; i >= 0; i--) {
      columnNames.add(0, HOLES_PREFIX + i);
    }
    return columnNames.toArray(new String[0]);
  }

  private List<Molecule> buildMolecules(Molecule molecule1, Molecule molecule2) {
    if (molecule2 == null && (molecule1.getOrientation() == Orientation.Left || molecule1.getOrientation() == Orientation.Right)) {
      molecule2 = molecule1.mirror(Direction.Right);
    }
    List<Molecule> moleculeVariants = getMoleculeVariants(molecule1);
    if (molecule2 != null) {
      moleculeVariants.addAll(getMoleculeVariants(molecule2));
    }
    return moleculeVariants;
  }

  private List<Molecule> getMoleculeVariants(Molecule molecule) {
    List<Molecule> moleculeVariants = new ArrayList<>();
    moleculeVariants.add(molecule);
    int rotations = molecule.getDistinctRotationCount() - 1;
    for (int i = 0; i < rotations; i++) {
      molecule = molecule.rotate();
      moleculeVariants.add(molecule);
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
      if (symmetry != null && !symmetry.hasPlacements(nodeId)) {
        continue;
      }
      for (Molecule m : molecules) {
        if (symmetry != null) {
          Map<Molecule, Integer> placements = symmetry.getPlacements(nodeId, m);
          safeAddRow(rows, buildRow(placements));
        }
        else {
          safeAddRow(rows, buildRow(nodeId, m));
        }
      }
      if (getHoleCount() > 0) {
        if (symmetry != null) {
          Set<Integer> holePlacements = symmetry.getHolePlacements(nodeId);
          int holeColumnCount = getHoleCount() / symmetry.getRotationalSymmetry();
          for (int i = 0; i < holeColumnCount; i++) {
            rows.add(new Row(holePlacements, i));
          }
        }
        else {
          for (int i = 0; i < getHoleCount(); i++) {
            rows.add(new Row(nodeId, i));
          }
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

  // build a row with symmetry constraint
  private Row buildRow(Map<Molecule, Integer> placements) {
    Set<Integer> allUsedIds = new HashSet<>();
    for (Map.Entry<Molecule, Integer> e : placements.entrySet()) {
      int nodeId = e.getValue();
      Molecule m = e.getKey();
      Node node = crystal.getNode(nodeId);
      Set<Integer> usedIds = m.getUsedNodeIds(crystal.getNode(nodeId));
      if (usedIds == null) {
        return null; // ran into hole
      }
      allUsedIds.addAll(m.getUsedNodeIds(crystal.getNode(nodeId)));
    }
    if (allUsedIds.size() != placements.size() * placements.keySet().iterator().next().size()) {
      // symmetry could cause nodes to overlap onto each other
      return null;
    }
    return new Row(placements, allUsedIds);
  }

  // build a row without symmetry constraint
  private Row buildRow(int nodeId, Molecule molecule) {
    Set<Integer> usedIds = molecule.getUsedNodeIds(crystal.getNode(nodeId));
    if (usedIds == null) {
      return null;
    }
    if (usedIds.size() != molecule.size()) {
      // for small unit cells, the molecules wrap around on them themselves
      return null;
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

  @SuppressWarnings("UnusedReturnValue")
  public CrystalSolver solve() {
    try {
      CrystalResultProcessor resultProcessor = new CrystalResultProcessor();
      if (matrix.length > 0) {
        ColumnObject h = DLX.buildSparseMatrix(matrix, columnNames);
        DLX.solve(h, true, resultProcessor);
      }
    }
    finally {
      results.done();
    }
    return this;
  }

  public class CrystalResultProcessor implements DLXResultProcessor {

    int lastOutput = 0;

    public boolean processResult(DLXResult dlxResult) {
      List<List<Row>> rowSets = convertResultToRowSets(dlxResult);
      rowSets.forEach(results::addResult);
      if (results.size() != lastOutput && results.size() % 100 == 0) {
        System.out.println("results=" + results.size());
        lastOutput = results.size();
      }
      // keep going unless it's time to quit
      return results.size() < maxSolutionCount && System.currentTimeMillis() < quitTime;
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
      Integer holeColumnIndex = objects.stream()
          .map(Object::toString)
          .filter(s -> !Character.isDigit(s.charAt(0)))
          .map(s -> Integer.parseInt(s.substring(1)))
          .findAny()
          .orElse(null);
      String rowKey = Row.buildKey(usedIds, holeColumnIndex);
      List<Row> rowsWithRowKey = rowKeyToRows.get(rowKey);
      if (rowsWithRowKey == null) {
        System.out.println("Null rows!");
      }
      matchingRowsInOrder.add(rowKeyToRows.get(rowKey));
    }
    return permute(matchingRowsInOrder, 0);
  }

  @SuppressWarnings({"ConstantConditions", "TypeParameterExplicitlyExtendsObject"})
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

  private static void solveCrystals(SolverParms parms) {
    for (int i = parms.getStartingCrystal(); i <= parms.getEndingCrystal(); i++) {
      File baseDir = new File(Utils.addTrailingSlash(parms.getInputDir()) + i);
      solveCrystal(parms, baseDir);
    }
  }

  private static void solveCrystal(SolverParms parms, File baseDir) {
    try {
      if (parms.isRequireSymmetry()) {
        Map<String, Symmetry> symmetries = Symmetry.readSymmetries(baseDir + SYMMETRIES_DIR);
        if (symmetries == null) {
          System.out.println("No symmetries available in dir " + baseDir);
          return;
        }
        for (Map.Entry<String, Symmetry> entry : symmetries.entrySet()) {
          try {
            Crystal crystal = new Crystal(baseDir, parms.getMolecule().size(), entry.getValue());
            new CrystalSolver(parms, crystal, entry.getValue()).solve();
          }
          catch (NoSolutionsException e) {
            System.out.println(e.getMessage());
          }
        }
      }
      else {
        Crystal crystal = new Crystal(baseDir, parms.getMolecule().size());
        new CrystalSolver(parms, crystal).solve();
      }
    }
    catch (RuntimeException e) {
      System.out.println("Failure solving crystal c" + baseDir.getName() + "-" + parms.getMolecule().getName() + " because of: " + e.getClass() + ": " + e.getLocalizedMessage());
      if (!(e instanceof IllegalArgumentException)) {
        e.printStackTrace();
      }
    }
  }

  private static final SolverParms DEFAULT_PARMS = new SolverParms("5", "/Users/merlin/Downloads/textfiles2/")
    .outputDir("/Users/merlin/Downloads/crystalResults");

  private static void doIt(String[] args) {
    try {
      SolverParms parms = new SolverParms(args);
      solveCrystals(parms);
    }
    catch (IllegalArgumentException e) {
      System.out.println("\nError: " + e.getMessage() + "\n");
      SolverParms.usage();
    }
  }

  public static void main(String[] args) {
//    doIt(args);
//    solveCrystals(new SolverParms(DEFAULT_PARMS).molecule(Molecule.dimer).endingCrystal(10).extraHoles(1));
//    solveCrystals(new SolverParms(DEFAULT_PARMS).molecule(Molecule.m05).molecule2(Molecule.m06).startingCrystal(22).endingCrystal(22));
//    solveCrystals(new SolverParms(DEFAULT_PARMS).molecule(Molecule.m09).crystal(426).extraHoles(5).quitAfter(SolverParms.HOUR));
//    solveCrystals(new SolverParms(DEFAULT_PARMS).molecule(Molecule.m09).crystal(358));
//    solveCrystals(new SolverParms(DEFAULT_PARMS).crystal(110).doGZip(true).molecule(Molecule.m09).molecule2(Molecule.m10));

//    solveCrystals(new SolverParms(DEFAULT_PARMS).molecule(Molecule.m05).crystal(378));
//    solveCrystals(new SolverParms(DEFAULT_PARMS).molecule(Molecule.m05).crystal(378).requireSymmetry(true));

//    solveCrystals(new SolverParms(DEFAULT_PARMS).molecule(Molecule.m09).crystal(166));
//    solveCrystals(new SolverParms(DEFAULT_PARMS).molecule(Molecule.m09).crystal(166).requireSymmetry(true));
    solveCrystals(new SolverParms(DEFAULT_PARMS).molecule(Molecule.m10).crystal(150).requireSymmetry(true));
    solveCrystals(new SolverParms(DEFAULT_PARMS).molecule(Molecule.m09).crystal(150).requireSymmetry(true));
    solveCrystals(new SolverParms(DEFAULT_PARMS).molecule(Molecule.m08).crystal(150).requireSymmetry(true));
    solveCrystals(new SolverParms(DEFAULT_PARMS).molecule(Molecule.m07).crystal(150).requireSymmetry(true));
    solveCrystals(new SolverParms(DEFAULT_PARMS).molecule(Molecule.m06).crystal(150).requireSymmetry(true));
    solveCrystals(new SolverParms(DEFAULT_PARMS).molecule(Molecule.m05).crystal(150).requireSymmetry(true));
    solveCrystals(new SolverParms(DEFAULT_PARMS).molecule(Molecule.m04).crystal(150).requireSymmetry(true));
    solveCrystals(new SolverParms(DEFAULT_PARMS).molecule(Molecule.m03).crystal(150).requireSymmetry(true));
    solveCrystals(new SolverParms(DEFAULT_PARMS).molecule(Molecule.m02).crystal(150).requireSymmetry(true));
    solveCrystals(new SolverParms(DEFAULT_PARMS).molecule(Molecule.m01).crystal(150).requireSymmetry(true));

  }

}
