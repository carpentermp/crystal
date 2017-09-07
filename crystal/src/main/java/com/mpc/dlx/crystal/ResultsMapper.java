package com.mpc.dlx.crystal;

import com.mpc.dlx.crystal.result.RatioResults;
import com.mpc.dlx.crystal.result.UnitCellResults;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class ResultsMapper {

  private final Molecule rootMolecule;
  private final Crystal crystal;

  public ResultsMapper(Molecule molecule, Crystal crystal) {
    this.rootMolecule = molecule;
    this.crystal = crystal;
  }

  public UnitCellResults map(Map<String, Set<CrystalResult>> results, Map<String, Integer> resultDuplicationCounts) {
    UnitCellResults unitCellResults = new UnitCellResults();
    unitCellResults.setMolecule(rootMolecule.getName());
    unitCellResults.setCrystal(crystal.getName());
    unitCellResults.setAdjacencyOrder(CrystalResult.computeAdjacencyOrder(rootMolecule));
    buildSitesAndCoordinates(unitCellResults);
    unitCellResults.setRatios(new ArrayList<>());
    List<String> ratios = results.keySet().stream().sorted().collect(Collectors.toList());
    for (String ratio : ratios) {
      unitCellResults.getRatios().add(mapRatioResults(unitCellResults.getSites(), ratio, results.get(ratio), resultDuplicationCounts));
    }
    return unitCellResults;
  }

  private void buildSitesAndCoordinates(UnitCellResults unitCellResults) {
    List<Integer> nodeIds = crystal.getNodeIds().stream().sorted().collect(Collectors.toList());
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
          dupCount = 0;
        }
        ratioResults.getDuplicates().add(dupCount);
      }
    }
    return ratioResults;
  }

  private List<Integer> mapBeads(List<Integer> nodeIds, CrystalResult cResult) {
    Map<Node, Integer> nodeToBeadIdMap = CrystalResult.buildNodeToBeadIdMap(crystal, cResult.getRows(), true);
    return nodeIds.stream().map(nodeId -> nodeToBeadIdMap.get(crystal.getNode(nodeId))).collect(Collectors.toList());
  }

  private List<Integer> mapPlacement(List<Integer> nodeIds, CrystalResult cResult) {
    Map<Integer, Row> nodeIdToRowMap = new HashMap<>();
    for (Row row : cResult.getRows()) {
      nodeIdToRowMap.put(row.getNodeId(), row);
    }
    List<Integer> placements = new ArrayList<>();
    for (Integer nodeId : nodeIds) {
      Row row = nodeIdToRowMap.get(nodeId);
      int rotation = 0;
      if (row != null) {
        rotation = row.getMolecule().getRotation().value();
      }
      placements.add(rotation);
    }
    return placements;
  }

}
