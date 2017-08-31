package com.mpc.dlx.crystal.result;

import java.util.List;

public class Result {

  private List<Placement> placements;
  private List<Adjacency> adjacencies;

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
