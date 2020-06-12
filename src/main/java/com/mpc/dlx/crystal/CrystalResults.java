package com.mpc.dlx.crystal;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "FieldCanBeLocal"})
public class CrystalResults {

  private static final String FN_BEADS = "beads.txt";
  private static final String FN_ADJACENCIES = "adjacencies.txt";
  private static final String FN_BONDS = "bonds.txt";
  private static final String FN_BOND_TYPES = "bond_types.txt";
  private static final String FN_TAGS = "tags.txt";
  private static final String FN_RATIOS = "ratios.txt";

  private final Molecule rootMolecule;
  private final Molecule rootMolecule2;
  private final Crystal crystal;
  private final List<Integer> nodeIds; // sorted list of nodes in the crystal unit cell
  private final int extraHoles;
  private final boolean dedup;
  private final boolean doGZip;
  private final Set<ResultEquality> resultsSeen = new HashSet<>();
  private final Map<String, Integer> bucketCounts = new HashMap<>();
  private int totalCount = 0;
  private final String baseDir;
  private String moleculeDir;
  private String outputDir;
  private String beadsFn;
  private String adjacenciesFn;
  private String bondsFn;
  private String bondTypesFn;
  private String tagsFn;
  private String ratiosFn;
  private BufferedWriter beadsWriter;
  private BufferedWriter adjacenciesWriter;
  private BufferedWriter bondsWriter;
  private BufferedWriter bondTypesWriter;
  private BufferedWriter tagsWriter;
  private BufferedWriter ratiosWriter;

  public CrystalResults(Molecule rootMolecule, Molecule rootMolecule2, Crystal crystal, int extraHoles, String baseDir, boolean dedup, boolean doGZip) {
    this.crystal = crystal;
    this.nodeIds = crystal.getAllNodeIdsSorted();
    this.rootMolecule = rootMolecule;
    this.rootMolecule2 = rootMolecule2;
    this.extraHoles = extraHoles;
    this.baseDir = baseDir;
    this.dedup = dedup;
    this.doGZip = doGZip;
    if (baseDir != null) {
      prepareForWritingResults();
    }
  }

  private void prepareForWritingResults() {
    try {
      this.moleculeDir = Utils.addTrailingSlash(Utils.createSubDir(baseDir, computeMoleculeDir(rootMolecule, rootMolecule2)).getAbsolutePath());
      this.outputDir = Utils.addTrailingSlash(Utils.createSubDir(moleculeDir, getOutputDirName()).getAbsolutePath());
      this.beadsFn = createFn(FN_BEADS);
      this.adjacenciesFn = createFn(FN_ADJACENCIES);
      this.bondsFn = createFn(FN_BONDS);
      this.bondTypesFn = createFn(FN_BOND_TYPES);
      this.tagsFn = createFn(FN_TAGS);
      this.ratiosFn = createFn(FN_RATIOS);
      this.beadsWriter = Utils.getWriter(beadsFn);
      this.adjacenciesWriter = Utils.getWriter(adjacenciesFn);
      this.bondsWriter = Utils.getWriter(bondsFn);
      this.bondTypesWriter = Utils.getWriter(bondTypesFn);
      this.tagsWriter = Utils.getWriter(tagsFn);
      this.ratiosWriter = Utils.getWriter(ratiosFn);
      outputHeader(beadsWriter, Utils.join(nodeIds, " "));
      outputHeader(adjacenciesWriter, getAdjacencyHeader());
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private String getAdjacencyHeader() {
    return Utils.join(rootMolecule2 == null ? rootMolecule.getAdjacencyOrder() : rootMolecule.getInterAdjacencyOrder(), " ");
  }

  static String computeMoleculeDir(Molecule rootMolecule, Molecule rootMolecule2) {
    if (rootMolecule2 == null) {
      return rootMolecule.getName();
    }
    return getMoleculeNameWithOrientation(rootMolecule) + "_" + getMoleculeNameWithOrientation(rootMolecule2);
  }

  private static String getMoleculeNameWithOrientation(Molecule molecule) {
    return molecule.getName() + molecule.getOrientation().name().substring(0, 1);
  }

  public void addResult(List<Row> rowSet) {
    try {
      CrystalResult result = new CrystalResult(crystal, rootMolecule, rootMolecule2, rowSet);
      if (dedup && resultsSeen.contains(result.getEquality())) {
        return;
      }
      outputResult(result);
      resultsSeen.add(result.getEquality());
      String bucket = result.getBucketName();
      Integer count = bucketCounts.get(bucket);
      if (count == null) {
        count = 0;
      }
      bucketCounts.put(bucket, ++count);
      totalCount++;
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void done() {
    System.out.println("For " + getOutputDirName() + " there were " + totalCount + " results!");
    for (Map.Entry<String, Integer> entry : bucketCounts.entrySet()) {
      System.out.println(entry.getKey() + ": " + entry.getValue());
    }
    System.out.println();
    try {
      safeClose(beadsWriter);
      safeClose(adjacenciesWriter);
      safeClose(bondsWriter);
      safeClose(bondTypesWriter);
      safeClose(tagsWriter);
      safeClose(ratiosWriter);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void safeClose(Writer writer) throws IOException {
    if (writer != null) {
      writer.close();
    }
  }

  private void outputResult(CrystalResult result) throws IOException {
    if (baseDir == null) {
      return;
    }
    beadsWriter.write(Utils.join(mapBeads(result), " ") + "\n");
    adjacenciesWriter.write(Utils.join(result.getAdjacencyCounts(), " ") + "\n");
    bondsWriter.write(Utils.join(mapBonds(result), " ") + "\n");
    bondTypesWriter.write(Utils.join(mapBondTypes(result), " ") + "\n");
    tagsWriter.write(result.getTag() + "\n");
    ratiosWriter.write(result.getBucketName() + "\n");
  }

  private String getOutputDirName() {
    return crystal.getName() + (extraHoles > 0 ? "h" + extraHoles : "");
  }

  private String createFn(String filename) {
    return outputDir + filename + (doGZip ? ".gz" : "");
  }

  private List<Integer> mapBeads(CrystalResult cResult) {
    return nodeIds.stream()
      .map(cResult::getBeadId)
      .collect(Collectors.toList());
  }

  private List<String> mapBonds(CrystalResult result) {
    List<String> bonds = new ArrayList<>();
    for (Row row : result.getRows()) {
      for (Molecule molecule : row.getMolecules()) {
        Node startingNode = crystal.getNode(row.getNodeId(molecule));
        List<BondKey> bondKeys = molecule.getBondKeys(startingNode);
        List<String> bondKeyIndices = bondKeys.stream()
          .map(crystal::getBondKeyIndex)
          .map(Object::toString)
          .collect(Collectors.toList());
        bonds.addAll(bondKeyIndices);
      }
    }
    return bonds;
  }

  private List<String> mapBondTypes(CrystalResult result) {
    List<String> bondTypes = new ArrayList<>();
    for (Row row : result.getRows()) {
      for (Molecule molecule : row.getMolecules()) {
        Node startingNode = crystal.getNode(row.getNodeId(molecule));
        List<BondKey> bondKeys = molecule.getBondKeys(startingNode);
        for (BondKey key : bondKeys) {
          int beadId1 = result.getBeadId(key.getFromNodeId());
          int beadId2 = result.getBeadId(key.getToNodeId());
          if (beadId1 > beadId2) {
            int temp = beadId1;
            beadId1 = beadId2;
            beadId2 = temp;
          }
          bondTypes.add(beadId1 + "-" + beadId2);
        }
      }
    }
    return bondTypes;
  }

  private void outputHeader(Writer writer, String headerLine) throws IOException {
    writer.write("# ");
    writer.write(headerLine);
    writer.write("\n");
  }

  public int size() {
    return resultsSeen.size();
  }

}
