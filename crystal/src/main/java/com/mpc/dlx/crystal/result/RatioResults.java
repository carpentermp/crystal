package com.mpc.dlx.crystal.result;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class RatioResults {

  private String ratio;
  private List<List<Integer>> beads;
  private List<List<Integer>> adjacencies;
  private List<List<Integer>> placements;
  private List<Integer> duplicates;

  public String getRatio() {
    return ratio;
  }

  public void setRatio(String ratio) {
    this.ratio = ratio;
  }

  public List<List<Integer>> getBeads() {
    return beads;
  }

  public void setBeads(List<List<Integer>> beads) {
    this.beads = beads;
  }

  public List<List<Integer>> getAdjacencies() {
    return adjacencies;
  }

  public void setAdjacencies(List<List<Integer>> adjacencies) {
    this.adjacencies = adjacencies;
  }

  public List<List<Integer>> getPlacements() {
    return placements;
  }

  public void setPlacements(List<List<Integer>> placements) {
    this.placements = placements;
  }

  public List<Integer> getDuplicates() {
    return duplicates;
  }

  public void setDuplicates(List<Integer> duplicates) {
    this.duplicates = duplicates;
  }

}
