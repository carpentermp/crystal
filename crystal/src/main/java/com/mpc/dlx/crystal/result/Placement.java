package com.mpc.dlx.crystal.result;

import java.util.List;

public class Placement {

  private String molecule;
  private String orientation;
  private List<Bead> beads;

  public String getMolecule() {
    return molecule;
  }

  public void setMolecule(String molecule) {
    this.molecule = molecule;
  }

  public String getOrientation() {
    return orientation;
  }

  public void setOrientation(String orientation) {
    this.orientation = orientation;
  }

  public List<Bead> getBeads() {
    return beads;
  }

  public void setBeads(List<Bead> beads) {
    this.beads = beads;
  }

}
