package com.mpc.dlx.crystal;

import au.id.bjf.dlx.DLXResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mpc.dlx.crystal.result.*;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"WeakerAccess", "squid:S1640", "squid:HiddenFieldCheck"})
public class CrystalResult {

  private static final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

  private final Map<Orientation, Integer> orientationCounts;
  private final List<Row> rows;

  public CrystalResult(DLXResult dlxResult, List<Row> allRows) {
    this.rows = convertResultToRows(dlxResult, allRows);
    this.orientationCounts = countOrientations(rows);
  }

  private List<Row> convertResultToRows(DLXResult result, List<Row> allRows) {
    List<Row> resultRows = new ArrayList<>();
    Iterator<List<Object>> it = result.rows();
    while (it.hasNext()) {
      List<Integer> usedIds = it.next()
          .stream()
          .map(String::valueOf)
          .map(Integer::parseInt)
          .collect(Collectors.toList());
      resultRows.add(findRow(usedIds, allRows));
    }
    return resultRows;
  }

  private Row findRow(List<Integer> usedIds, List<Row> allRows) {
    return allRows.stream()
        .filter(r -> r.isThisRow(usedIds))
        .findAny()
        .orElse(null);
  }

  private Map<Orientation, Integer> countOrientations(List<Row> resultRows) {
    Map<Orientation, Integer> orientationCounts = new HashMap<>();
    for (Row row : resultRows) {
      Orientation orientation = row.getMolecule().getOrientation();
      Integer count = orientationCounts.get(orientation);
      if (count == null) {
        count = 0;
      }
      orientationCounts.put(orientation, ++count);
    }
    return orientationCounts;
  }

  public String getBucketName() {
    Orientation anOrientation = rows.get(0).getMolecule().getOrientation();
    if (anOrientation == Orientation.AChiral || anOrientation == Orientation.Symmetric) {
      return "achiral";
    }
    return String.format("l%1$02dr%2$02d", getCountOfOrientation(Orientation.Left), getCountOfOrientation(Orientation.Right));
  }

  private int getCountOfOrientation(Orientation orientation) {
    Integer count = orientationCounts.get(orientation);
    return count == null ? 0 : count;
  }

  public List<Row> getRows() {
    return Collections.unmodifiableList(rows);
  }

  public String toJson(Crystal crystal, Molecule molecule) {
    Result result = new Result();
    result.setMolecule(molecule.getName());
    result.setPlacements(new ArrayList<>());
    for (Row row : rows) {
      result.getPlacements().add(buildPlacement(crystal, molecule, row));
    }
    // todo add stuff for adjacencies
    return gson.toJson(result);
  }

  private Placement buildPlacement(Crystal crystal, Molecule molecule, Row row) {
    Placement placement = new Placement();
    placement.setOrientation(molecule.getOrientation().name());
    placement.setBeads(new ArrayList<>());
    // todo beads
    return placement;
  }

}
