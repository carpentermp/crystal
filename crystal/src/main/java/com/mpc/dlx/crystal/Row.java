package com.mpc.dlx.crystal;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public class Row {

  private final int nodeId;
  private final Molecule molecule;
  private final Set<Integer> usedIds;

  public Row(int nodeId, Molecule molecule, Set<Integer> usedIds) {
    this.nodeId = nodeId;
    this.molecule = molecule;
    this.usedIds = usedIds;
  }

  public byte[] getBytes() {
    if (usedIds == null) {
      return null;
    }
    byte[] row = new byte[CrystalSolver.COLUMN_COUNT];
    for (int i = 0; i < CrystalSolver.COLUMN_COUNT; i++) {
      row[i] = (byte) (usedIds.contains(i + 1) ? 1 : 0);
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

}
