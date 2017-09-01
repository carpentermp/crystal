package com.mpc.dlx.crystal;

import au.id.bjf.dlx.DLXResult;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class CrystalResult {

  private final int leftCount;
  private final List<Row> rows;

  public CrystalResult(DLXResult dlxResult, List<Row> allRows) {
    this.rows = convertResultToRows(dlxResult, allRows);
    this.leftCount = countLeftOrientedMolecules(rows);
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

  private int countLeftOrientedMolecules(List<Row> resultRows) {
    return resultRows.stream()
        .filter(r -> r.getMolecule().getOrientation() == Orientation.Left)
        .collect(Collectors.toList())
        .size();
  }

  public int getLeftCount() {
    return leftCount;
  }

  public int getRightCount() {
    return rows.size() - leftCount;
  }

  public List<Row> getRows() {
    return Collections.unmodifiableList(rows);
  }

}
