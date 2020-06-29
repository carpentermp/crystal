package com.mpc.dlx.crystal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("WeakerAccess")
public class RootMolecules {

  private final Molecule molecule1;
  private final Molecule molecule2;
  private final boolean doEnantiomers;

  public RootMolecules(Molecule molecule1, Molecule molecule2, boolean doEnantiomers) {
    Objects.requireNonNull(molecule1);
    if (molecule2 != null) {
      if (molecule1.size() != molecule2.size()) {
        throw new IllegalArgumentException("Molecules must be same size");
      }
      if (molecule1.equals(molecule2)) {
        throw new IllegalArgumentException("If two molecules are given, they must be different");
      }
      if (molecule1.getName().compareTo(molecule2.getName()) > 0) {
        Molecule temp = molecule1;
        molecule1 = molecule2;
        molecule2 = temp;
      }
    }
    this.molecule1 = molecule1;
    this.molecule2 = molecule2;
    this.doEnantiomers = doEnantiomers;
    if (doEnantiomers) {
      if (!molecule1.isChiral()) {
        throw new IllegalArgumentException("Molecule " + molecule1.getName() + " is not chiral");
      }
      if (molecule2 != null && !molecule2.isChiral()) {
        throw new IllegalArgumentException("Molecule " + molecule2.getName() + " is not chiral");
      }
    }
  }

  public Molecule getMolecule1() {
    return molecule1;
  }

  public Molecule getMolecule2() {
    return molecule2;
  }

  public int moleculeSize() {
    return molecule1.size();
  }

  public int moleculeCount() {
    return molecule2 == null ? 1 : 2;
  }

  public boolean twoMoleculesHaveSameSpecificOrientation() {
    return !doEnantiomers && molecule2 != null && molecule1.getOrientation() == molecule2.getOrientation();
  }

  public boolean twoMoleculesWithEnantiomers() {
    return doEnantiomers && molecule2 != null;
  }

  public int getDistinctBeadIdOffset(Molecule molecule) {
    int orientationOffset = isOrientedRight(molecule) ? getAdjacencyBeadCount() : 0;
    if (molecule2 == null) {
      return orientationOffset;
    }
    int moleculeOffset = getAdjacencyBeadIdOffset(molecule);
    if (!doEnantiomers) {
      return moleculeOffset;
    }
    return orientationOffset + moleculeOffset;
  }

  public int getDistinctBeadCount() {
    return doEnantiomers ? getAdjacencyBeadCount() * 2 : getAdjacencyBeadCount();
  }

  public int getAdjacencyBeadIdOffset(Molecule molecule) {
    return isHighMolecule(molecule) ? molecule.size() : 0;
  }

  public int getAdjacencyBeadCount() {
    return moleculeSize() * moleculeCount();
  }

  public String getAdjacencyHeader() {
    return Utils.join(molecule2 == null ? molecule1.getAdjacencyOrder() : molecule1.getInterAdjacencyOrder(), " ");
  }

  public boolean isHighMolecule(Molecule molecule) {
    return molecule2 != null && molecule2.getName().equals(molecule.getName());
  }

  private boolean isOrientedRight(Molecule molecule) {
    return molecule.getOrientation() == Orientation.Right;
  }

  public String getName() {
    StringBuilder sb = new StringBuilder();
    sb.append(molecule1.getName());
    if (molecule1.isChiral() && !doEnantiomers) {
      sb.append(getOrientationChar(molecule1));
    }
    if (molecule2 != null) {
      sb.append("_")
        .append(molecule2.getName());
      if (molecule2.isChiral() && !doEnantiomers) {
        sb.append(getOrientationChar(molecule2));
      }
    }
    return sb.toString();
  }

  public List<Molecule> asList() {
    List<Molecule> rtn = new ArrayList<>();
    rtn.add(molecule1);
    if (molecule2 != null) {
      rtn.add(molecule2);
    }
    if (doEnantiomers) {
      rtn.add(molecule1.enantiomer());
      if (molecule2 != null) {
        rtn.add(molecule2.enantiomer());
      }
    }
    return rtn;
  }

  private static String getOrientationChar(Molecule molecule) {
    return molecule.getOrientation().name().substring(0, 1);
  }

}
