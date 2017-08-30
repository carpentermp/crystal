package com.mpc.dlx.crystal;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings({"WeakerAccess", "squid:S1168"})
public class Row {

  private final int nodeId;
  private final Molecule molecule;
  private final Set<Integer> usedIds;

  public Row(int nodeId, Molecule molecule, Set<Integer> usedIds) {
    this.nodeId = nodeId;
    this.molecule = molecule;
    this.usedIds = usedIds;
  }

  public byte[] getBytes(String[] columns) {
    if (usedIds == null) {
      return null;
    }
    byte[] row = new byte[columns.length];
    for (int i = 0; i < columns.length; i++) {
      row[i] = (byte) (usedIds.contains(Integer.parseInt(columns[i])) ? 1 : 0);
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
