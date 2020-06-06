package com.mpc.dlx.crystal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * represents a particular symmetry for a particular "crystal" or lattice
 */
@SuppressWarnings("WeakerAccess")
public class Symmetry {

  private final String name;
  private Map<Integer, Map<Direction, List<Nod>>> symmetricPlacements = new LinkedHashMap<>();
  private Set<Integer> requiredHoles = new HashSet<>();

  public Symmetry(File file) throws IOException {
    String name = file.getName();
    if (name.endsWith(".txt")) {
      name = name.substring(0, name.length() - ".txt".length());
    }
    this.name = name;
    groupify(file).forEach(this::processGroup);
  }

  public String getName() {
    return name;
  }

  public int getRotationalSymmetry() {
    return symmetricPlacements.values().iterator().next().values().iterator().next().size();
  }

  public boolean hasPlacements(int nodeId) {
    return symmetricPlacements.containsKey(nodeId);
  }

  public Map<Molecule, Integer> getPlacements(int nodeId, Molecule molecule) {
    Map<Direction, List<Nod>> map = symmetricPlacements.get(nodeId);
    if (map == null) {
      return null;
    }
    List<Nod> nods = map.get(molecule.getDirections().get(0));
    if (nods == null) {
      throw new RuntimeException("Unexpectedly, we didn't get any Nods!");
    }
    Map<Molecule, Integer> rtn = new HashMap<>();
    for (Nod nod : nods) {
      Molecule m = nodToMolecule(nod, molecule);
      rtn.put(m, nod.nodeId);
    }
    return rtn;
  }

  public Set<Integer> getHolePlacements(int nodeId) {
    Map<Direction, List<Nod>> map = symmetricPlacements.get(nodeId);
    if (map == null) {
      return null;
    }
    // all the directions are the same as far as the nodeIds go, so
    // just get the first List<Nod> from any direction.
    return map.values().iterator().next()
      .stream()
      .map(n -> n.nodeId)
      .collect(Collectors.toSet());
  }

  public Set<Integer> getRequiredHoles() {
    return Collections.unmodifiableSet(requiredHoles);
  }

  private Molecule nodToMolecule(Nod nod, Molecule initialMolecule) {
    Molecule m = nod.isMirrored ? initialMolecule.mirror(initialMolecule.getRotation()) : initialMolecule;
    while (m.getRotation() != nod.direction) {
      m = m.rotate();
    }
    return m;
  }

  private List<Group> groupify(File file) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(file));
    String line;
    List<Group> groups = new ArrayList<>();
    Group currentGroup = null;
    while ((line = reader.readLine()) != null) {
      if (line.trim().isEmpty()) {
        continue;
      }
      Row row = new Row(line);
      if (row.isHole) {
        requiredHoles.add(row.nodeId);
        continue;
      }
      if (currentGroup == null || row.groupId != currentGroup.getGroupId()) {
        currentGroup = new Group(row);
        groups.add(currentGroup);
        continue;
      }
      currentGroup.addRow(row);
    }
    return groups;
  }

  private void processGroup(Group group) {
    for (Row row : group.getRows()) {
      if (row.nodeId == row.groupId) {
        Map<Direction, List<Nod>> map = new HashMap<>();
        symmetricPlacements.put(row.nodeId, map);
        for (int i = 0; i < row.orientations.size(); i++) {
          Direction direction = Direction.fromValue(i + 1);
          List<Nod> nodList = new ArrayList<>();
          nodList.add(new Nod(row.nodeId, false, direction));
          map.put(direction, nodList);
        }
      }
      else {
        Map<Direction, List<Nod>> map = symmetricPlacements.get(row.groupId);
        for (int i = 0; i < row.orientations.size(); i++) {
          Direction symmetricDirection = Direction.fromValue(row.orientations.get(i));
          map.get(Direction.fromValue(i + 1)).add(new Nod(row.nodeId, row.isMirrored, symmetricDirection));
        }
      }
    }
  }

  /**
   * represents the placement of a "symmetrical molecule", it's node ID, orientation, and initial direction relative to the initial molecule
   */
  @SuppressWarnings("WeakerAccess")
  public static class Nod {

    public final int nodeId;
    public final boolean isMirrored;
    public final Direction direction;

    public Nod(int nodeId, boolean isMirrored, Direction direction) {
      this.nodeId = nodeId;
      this.isMirrored = isMirrored;
      this.direction = direction;
    }

  }

  public static class Group {

    private final List<Row> rows = new ArrayList<>();

    public Group(Row initialRow) {
      rows.add(initialRow);
    }

    public void addRow(Row row) {
      if (row.groupId == row.nodeId) {
        rows.add(0, row);
      }
      else {
        rows.add(row);
      }
    }

    public int getGroupId() {
      return rows.get(0).groupId;
    }

    public List<Row> getRows() {
      return Collections.unmodifiableList(rows);
    }

  }

  public static class Row {

    public final int groupId;
    public final int nodeId;
    public final boolean isHole;
    public final boolean isMirrored;
    public final List<Integer> orientations = new ArrayList<>();

    public Row(String line) {
      List<Integer> ints = lineToInts(line);
      groupId = ints.get(0);
      nodeId = ints.get(1);
      isHole = ints.get(2) != 0;
      isMirrored = ints.get(3) != 0;
      for (int i = 4; i < ints.size(); i++) {
        orientations.add(ints.get(i));
      }
    }

    private List<Integer> lineToInts(String line) {
      return Arrays.stream(line.split(" "))
        .map(Integer::parseInt)
        .collect(Collectors.toList());
    }

  }

  public static Map<String, Symmetry> readSymmetries(String baseDir) {
    try {
      File dir = new File(baseDir);
      if (!dir.exists()) {
        return null;
      }
      File[] files = dir.listFiles();
      if (files == null) {
        return null;
      }
      Map<String, Symmetry> symmetries = new HashMap<>();
      for (File file : files) {
        if (!file.getName().endsWith(".txt")) {
          continue;
        }
        Symmetry symmetry = new Symmetry(file);
        symmetries.put(symmetry.getName(), symmetry);
      }
      return symmetries;
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

}
