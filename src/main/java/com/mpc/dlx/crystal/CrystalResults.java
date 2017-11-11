package com.mpc.dlx.crystal;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "FieldCanBeLocal"})
public class CrystalResults {

  private static final String FN_BEADS = "beads.txt";
  private static final String FN_ADJACENCIES = "adjacencies.txt";
  private static final String FN_PLACEMENTS = "placements.txt";
  private static final String FN_TAGS = "tags.txt";
  private static final String FN_RATIOS = "ratios.txt";

  private final Molecule rootMolecule;
  private final Crystal crystal;
  private final List<Integer> nodeIds;
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
  private String placementsFn;
  private String tagsFn;
  private String ratiosFn;
  private BufferedWriter beadsWriter;
  private BufferedWriter adjacenciesWriter;
  private BufferedWriter placementsWriter;
  private BufferedWriter tagsWriter;
  private BufferedWriter ratiosWriter;

  public CrystalResults(Molecule rootMolecule, Crystal crystal, int extraHoles, String baseDir, boolean dedup, boolean doGZip) {
    this.crystal = crystal;
    this.nodeIds = buildNodeIdsArray();
    this.rootMolecule = rootMolecule;
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
      this.moleculeDir = Utils.addTrailingSlash(Utils.createSubDir(baseDir, rootMolecule.getName()).getAbsolutePath());
      this.outputDir = Utils.addTrailingSlash(Utils.createSubDir(moleculeDir, getOutputDirName()).getAbsolutePath());
      this.beadsFn = createFn(FN_BEADS);
      this.adjacenciesFn = createFn(FN_ADJACENCIES);
      this.placementsFn = createFn(FN_PLACEMENTS);
      this.tagsFn = createFn(FN_TAGS);
      this.ratiosFn = createFn(FN_RATIOS);
      this.beadsWriter = Utils.getWriter(beadsFn);
      this.adjacenciesWriter = Utils.getWriter(adjacenciesFn);
      this.placementsWriter = Utils.getWriter(placementsFn);
      this.tagsWriter = Utils.getWriter(tagsFn);
      this.ratiosWriter = Utils.getWriter(ratiosFn);
      String headerForBeadsAndPlacements = Utils.join(nodeIds, " ");
      outputHeader(beadsWriter, headerForBeadsAndPlacements);
      outputHeader(adjacenciesWriter, Utils.join(rootMolecule.getAdjacencyOrder(), " "));
      outputHeader(placementsWriter, headerForBeadsAndPlacements);
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void addResult(List<Row> rowSet) {
    try {
      CrystalResult result = new CrystalResult(crystal, rootMolecule, rowSet);
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
      beadsWriter.close();
      adjacenciesWriter.close();
      placementsWriter.close();
      tagsWriter.close();
      ratiosWriter.close();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void outputResult(CrystalResult result) throws IOException {
    if (baseDir == null) {
      return;
    }
    beadsWriter.write(Utils.join(mapBeads(nodeIds, result), " ") + "\n");
    adjacenciesWriter.write(Utils.join(result.getAdjacencyCounts(), " ") + "\n");
    placementsWriter.write(Utils.join(mapPlacement(nodeIds, result), " ") + "\n");
    tagsWriter.write(result.getTag() + "\n");
    ratiosWriter.write(result.getBucketName() + "\n");
  }

  private String getOutputDirName() {
    return crystal.getName() + (extraHoles > 0 ? "h" + extraHoles : "");
  }

  private String createFn(String filename) {
    return outputDir + filename + (doGZip ? ".gz" : "");
  }

  private List<Integer> mapBeads(List<Integer> nodeIds, CrystalResult cResult) {
    Map<Node, Integer> nodeToBeadIdMap = CrystalResult.buildNodeToBeadIdMap(crystal, cResult.getRows(), true);
    return nodeIds.stream()
      .map(nodeId -> {
        Integer beadId = nodeToBeadIdMap.get(crystal.getNode(nodeId));
        if (beadId == null) {
          beadId = 0;
        }
        return beadId;
      })
      .collect(Collectors.toList());
  }

  private List<Integer> mapPlacement(List<Integer> nodeIds, CrystalResult cResult) {
    Map<Integer, Row> nodeIdToRowMap = new HashMap<>();
    for (Row row : cResult.getRows()) {
      nodeIdToRowMap.put(row.getNodeId(), row);
    }
    List<Integer> placements = new ArrayList<>();
    for (Integer nodeId : nodeIds) {
      Row row = nodeIdToRowMap.get(nodeId);
      int rotation = Direction.Right.value(); // it's the removed origin hole
      if (row != null) {
        rotation = row.getMolecule().getRotation().value();
      }
      placements.add(rotation);
    }
    return placements;
  }

  private List<Integer> buildNodeIdsArray() {
    List<Integer> nodeIds = new ArrayList<>();
    Node removedNode = crystal.getRemovedNode();
    if (removedNode != null) {
      nodeIds.add(removedNode.getId());
    }
    nodeIds.addAll(crystal.getNodeIds().stream().sorted().collect(Collectors.toList()));
    List<Integer> allNodeIds = new ArrayList<>();
    for (int i = 0; i < 9; i++) {
      allNodeIds.addAll(nodeIds);
    }
    return allNodeIds;
  }

  private void outputHeader(Writer writer, String headerLine) throws IOException {
    writer.write("# ");
    writer.write(headerLine);
    writer.write("\n");
  }

}
