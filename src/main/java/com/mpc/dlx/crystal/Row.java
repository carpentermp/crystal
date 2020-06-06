package com.mpc.dlx.crystal;

import java.util.*;
import java.util.stream.Collectors;

/**
 * represents a candidate for placement of a molecule (or list of molecules, if symmetry is required) in the crystal
 */
@SuppressWarnings({"WeakerAccess", "squid:S1168"})
public class Row {

  private Map<Molecule, Integer> moleculeToNodeId = new LinkedHashMap<>();
  private final Set<Integer> usedIds;
  private final Integer holeColumnIndex;
  private final String key;

  /**
   * normal constructor for a candidate placement of a molecule at a specific lattice site
   * @param nodeId the starting "node id" of the lattice site where the molecule was placed
   * @param molecule the molecule that was placed there
   * @param usedIds all the column indexes of the lattice sites take by the placement of the molecule in the given place
   */
  public Row(int nodeId, Molecule molecule, Set<Integer> usedIds) {
    moleculeToNodeId.put(molecule, nodeId);
    this.usedIds = usedIds;
    this.holeColumnIndex = null;
    this.key = buildKey(usedIds, null);
  }

  /**
   * constructor for when symmetry is required
   * @param placements map of nodeIds to molecule that represents the placments of the molecules
   * @param usedIds the complete set of usedIds (all molecules)
   */
  public Row(Map<Molecule, Integer> placements, Set<Integer> usedIds) {
    this.moleculeToNodeId.putAll(placements);
    this.usedIds = usedIds;
    this.holeColumnIndex = null;
    this.key = buildKey(usedIds, null);
  }

  /**
   * constructor used to create potential "holes" in the result
   * @param holeNodeId the node id of the hole
   * @param holeColumnIndex the "index" of the hole (to distinguish the hole from other holes)
   */
  public Row(int holeNodeId, int holeColumnIndex) {
    this.moleculeToNodeId.put(Molecule.hole, holeNodeId);
    this.usedIds = new HashSet<>(Collections.singletonList(holeNodeId));
    this.holeColumnIndex = holeColumnIndex;
    this.key = buildKey(usedIds, holeColumnIndex);
  }

  /**
   * constructor used to create potential "holes" in the result
   * @param holeNodeIds the node ids of the holes being placed
   * @param holeColumnIndex the "index" of the hole (to distinguish the hole from other holes)
   */
  public Row(Set<Integer> holeNodeIds, int holeColumnIndex) {
    holeNodeIds.forEach(id -> this.moleculeToNodeId.put(Molecule.hole, id));
    this.usedIds = new HashSet<>(holeNodeIds);
    this.holeColumnIndex = holeColumnIndex;
    this.key = buildKey(usedIds, holeColumnIndex);
  }

  public static String buildKey(Collection<Integer> usedIds, Integer holeColumnIndex) {
    String joinedSortedUsedIds = Utils.join(usedIds.stream().sorted().collect(Collectors.toList()), "-");
    if (holeColumnIndex == null) {
      return joinedSortedUsedIds;
    }
    return "h" + holeColumnIndex + "-" + joinedSortedUsedIds;
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
      row[holeColumnIndex] = 1;
    }
    return row;
  }

  public int getNodeId(Molecule molecule) {
    return moleculeToNodeId.get(molecule);
  }

  public Set<Molecule> getMolecules() {
    return moleculeToNodeId.keySet();
  }

  @SuppressWarnings("unused")
  public Set<Integer> getUsedIds() {
    return Collections.unmodifiableSet(usedIds);
  }

  public boolean isHole() {
    return holeColumnIndex != null;
  }

  public String getKey() {
    return key;
  }

}
