package com.mpc.dlx.crystal;

import au.id.bjf.dlx.DLXResult;
import au.id.bjf.dlx.DLXResultProcessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("WeakerAccess")
public class CrystalResultProcessor implements DLXResultProcessor {

  private int count = 0;
  private int[] leftCounts = new int[13];
  private final List<Row> rows;

  public CrystalResultProcessor(List<Row> rows) {
    this.rows = rows;
  }

  private List<Row> convertResultToRows(DLXResult result) {
    List<Row> resultRows = new ArrayList<>();
    Iterator<List<Object>> it = result.rows();
    while (it.hasNext()) {
      List<Integer> usedIds = it.next()
          .stream()
          .map(String::valueOf)
          .map(s -> Integer.parseInt(s.substring(1)))
          .collect(Collectors.toList());
      resultRows.add(findRow(usedIds));
    }
    return resultRows;
  }

  private Row findRow(List<Integer> usedIds) {
    return rows.stream()
        .filter(r -> r.isThisRow(usedIds))
        .findAny()
        .orElse(null);
  }

  public boolean processResult(DLXResult result) {
    count++;
    List<Row> resultRows = convertResultToRows(result);
    int leftCount = countLeftOrientedMolecules(resultRows);
    leftCounts[leftCount]++;
    return true; // keep going
  }

  private int countLeftOrientedMolecules(List<Row> resultRows) {
    return resultRows.stream()
        .filter(r -> r.getMolecule().getOrientation() == Orientation.Left)
        .collect(Collectors.toList())
        .size();
  }

  public int getCount() {
    return count;
  }

  public int[] getLeftCounts() {
    return leftCounts;
  }

}
