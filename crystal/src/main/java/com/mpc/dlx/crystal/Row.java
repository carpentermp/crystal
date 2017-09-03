package com.mpc.dlx.crystal;

import java.util.*;

@SuppressWarnings({"WeakerAccess", "squid:S1168"})
public class Row {

  private final int nodeId;
  private final Molecule molecule;
  private final Set<Integer> usedIds;
  private final Integer holeIndex;

  public Row(int nodeId, Molecule molecule, Set<Integer> usedIds) {
    this.nodeId = nodeId;
    this.molecule = molecule;
    this.usedIds = usedIds;
    this.holeIndex = null;
  }

  public Row(int holeNodeId, int holeIndex) {
    this.nodeId = holeNodeId;
    this.molecule = Molecule.hole;
    this.usedIds = new HashSet<>(Collections.singletonList(holeNodeId));
    this.holeIndex = holeIndex;
  }

  public byte[] getBytes(String[] columns) {
    if (usedIds == null) {
      return null;
    }
    byte[] row = new byte[columns.length];
    for (int i = 0; i < columns.length; i++) {
      String columnName = columns[i];
      if (!Character.isDigit(columnName.charAt(0))) {
        continue;
      }
      row[i] = (byte) (usedIds.contains(Integer.parseInt(columnName)) ? 1 : 0);
    }
    if (isHole()) {
      row[holeIndex] = 1;
    }
    return row;
  }

  public int getNodeId() {
    return nodeId;
  }

  public Molecule getMolecule() {
    return molecule;
  }

  public Set<Integer> getUsedIds() {
    return Collections.unmodifiableSet(usedIds);
  }

  public boolean isThisRow(Collection<Integer> ids) {
    return !(ids == null || ids.size() != usedIds.size()) && ids.stream().allMatch(usedIds::contains);
  }

  public boolean isHole() {
    return holeIndex != null;
  }

}
