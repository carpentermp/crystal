package com.mpc.dlx.crystal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mpc.dlx.crystal.result.RatioResults;
import com.mpc.dlx.crystal.result.UnitCellResults;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class ResultsWriter {

  private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

  private final Molecule rootMolecule;
  private final Crystal crystal;

  public ResultsWriter(Molecule molecule, Crystal crystal) {
    this.rootMolecule = molecule;
    this.crystal = crystal;
  }

  public void write(Map<String, Set<CrystalResult>> results, Map<String, Integer> resultDuplicationCounts, Writer writer) throws IOException {
    UnitCellResults unitCellResults = new UnitCellResults();
    unitCellResults.setMolecule(rootMolecule.getName());
    unitCellResults.setCrystal(crystal.getName());
    unitCellResults.setAdjacencyOrder(rootMolecule.getAdjacencyOrder());
    buildSitesAndCoordinates(unitCellResults);
    unitCellResults.setRatios(new ArrayList<>());

    // write the beginning part
    StringWriter buffer = new StringWriter();
    gson.toJson(unitCellResults, buffer);
    String unitCellStr = buffer.toString();
    int index = unitCellStr.lastIndexOf(']');
    writer.write(unitCellStr.substring(0, index));

    // now write the ratios, one at a time
    List<String> ratios = results.keySet().stream().sorted().collect(Collectors.toList());
    for (int i = 0; i < ratios.size(); i++) {
      String ratio = ratios.get(i);
      RatioResults ratioResults = mapRatioResults(unitCellResults.getSites(), ratio, results.get(ratio), resultDuplicationCounts);
      if (i != 0) {
        writer.write(',');
      }
      gson.toJson(ratioResults, writer);
    }

    // now write the trailing part
    writer.write(unitCellStr.substring(index));
  }

  private void buildSitesAndCoordinates(UnitCellResults unitCellResults) {
    List<Integer> nodeIds = new ArrayList<>();
    Node removedNode = crystal.getRemovedNode();
    if (removedNode != null) {
      nodeIds.add(removedNode.getId());
    }
    nodeIds.addAll(crystal.getNodeIds().stream().sorted().collect(Collectors.toList()));
    List<Integer> allNodeIds = new ArrayList<>();
    List<List<Double>> coordinates = new ArrayList<>();
    for (int i = 0; i < 9; i++) {
      allNodeIds.addAll(nodeIds);
      for (Integer nodeId : nodeIds) {
        coordinates.add(crystal.getCoordinates(nodeId).get(i).toList());
      }
    }
    unitCellResults.setSites(allNodeIds);
    unitCellResults.setSiteCoordinates(coordinates);
  }

  private RatioResults mapRatioResults(List<Integer> nodeIds, String ratio, Set<CrystalResult> cResults, Map<String, Integer> resultDuplicateCounts) {
    RatioResults ratioResults = new RatioResults();
    ratioResults.setRatio(ratio);
    ratioResults.setBeads(new ArrayList<>());
    ratioResults.setAdjacencies(new ArrayList<>());
    ratioResults.setPlacements(new ArrayList<>());
    if (resultDuplicateCounts != null) {
      ratioResults.setDuplicates(new ArrayList<>());
    }
    for (CrystalResult cResult : cResults) {
      ratioResults.getBeads().add(mapBeads(nodeIds, cResult));
      ratioResults.getAdjacencies().add(cResult.getAdjacencyCounts());
      ratioResults.getPlacements().add(mapPlacement(nodeIds, cResult));
      if (resultDuplicateCounts != null) {
        Integer dupCount = resultDuplicateCounts.get(cResult.toString());
        if (dupCount == null) {
          dupCount = 1;
        }
        ratioResults.getDuplicates().add(dupCount);
      }
    }
    return ratioResults;
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

}
