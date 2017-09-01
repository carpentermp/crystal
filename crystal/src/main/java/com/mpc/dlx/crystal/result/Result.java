package com.mpc.dlx.crystal.result;

import java.util.List;

public class Result {

  private String crystal;
  private String molecule;
  private List<Placement> placements;
  private List<Adjacency> adjacencies;

  public String getCrystal() {
    return crystal;
  }

  public void setCrystal(String crystal) {
    this.crystal = crystal;
  }

  public String getMolecule() {
    return molecule;
  }

  public void setMolecule(String molecule) {
    this.molecule = molecule;
  }

  public List<Placement> getPlacements() {
    return placements;
  }

  public void setPlacements(List<Placement> placements) {
    this.placements = placements;
  }

  public List<Adjacency> getAdjacencies() {
    return adjacencies;
  }

  public void setAdjacencies(List<Adjacency> adjacencies) {
    this.adjacencies = adjacencies;
  }

}
