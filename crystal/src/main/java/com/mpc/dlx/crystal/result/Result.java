package com.mpc.dlx.crystal.result;

import java.util.List;

public class Result {

  private String crystal;
  private String molecule;
  private List<Placement> placements;
  private List<String> adjacencyOrder;
  private List<Integer> adjacencyCounts;

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

  public List<String> getAdjacencyOrder() {
    return adjacencyOrder;
  }

  public void setAdjacencyOrder(List<String> adjacencyOrder) {
    this.adjacencyOrder = adjacencyOrder;
  }

  public List<Integer> getAdjacencyCounts() {
    return adjacencyCounts;
  }

  public void setAdjacencyCounts(List<Integer> adjacencyCounts) {
    this.adjacencyCounts = adjacencyCounts;
  }
}
