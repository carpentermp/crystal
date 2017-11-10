package com.mpc.dlx.crystal;

import java.util.*;
import java.util.stream.Collectors;

/**
 * represents a candidate for placement of a molecule in the crystal
 */
@SuppressWarnings({"WeakerAccess", "squid:S1168"})
public class Row {

  private final int nodeId;
  private final Molecule molecule;
  private final Set<Integer> usedIds;
  private final Integer holeIndex;
  private final String key;

  /**
   * normal constructor for a candidate placement of a molecule at a specific lattice site
   * @param nodeId the starting "node id" of the lattice site where the molecule was placed
   * @param molecule the molecule that was placed there
   * @param usedIds all the column indexes of the lattice sites take by the placement of the molecule in the given place
   */
  public Row(int nodeId, Molecule molecule, Set<Integer> usedIds) {
    this.nodeId = nodeId;
    this.molecule = molecule;
    this.usedIds = usedIds;
    this.holeIndex = null;
    this.key = buildKey(usedIds, null);
  }

  public static String buildKey(Collection<Integer> usedIds, Integer holeIndex) {
    if (holeIndex != null) {
      return "h" + holeIndex + "-" + usedIds.iterator().next();
    }
    return Utils.join(usedIds.stream().sorted().collect(Collectors.toList()), "-");
  }

  /**
   * constructor used to create potential "holes" in the result
   * @param holeNodeId the node id of the hole
   * @param holeIndex the "index" of the hole (to distinguish the hole from other holes)
   */
  public Row(int holeNodeId, int holeIndex) {
    this.nodeId = holeNodeId;
    this.molecule = Molecule.hole;
    this.usedIds = new HashSet<>(Collections.singletonList(holeNodeId));
    this.holeIndex = holeIndex;
    this.key = buildKey(usedIds, holeIndex);
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

  public boolean isHole() {
    return holeIndex != null;
  }

  public String getKey() {
    return key;
  }

}
