package com.mpc.dlx.crystal.result;

import java.util.List;

public class UnitCellResults {

  private String molecule;
  private String crystal;
  private List<String> adjacencyOrder;
  private List<Integer> sites;
  private List<List<Double>> siteCoordinates;
  private List<RatioResults> ratios;

  public String getMolecule() {
    return molecule;
  }

  public void setMolecule(String molecule) {
    this.molecule = molecule;
  }

  public String getCrystal() {
    return crystal;
  }

  public void setCrystal(String crystal) {
    this.crystal = crystal;
  }

  public List<String> getAdjacencyOrder() {
    return adjacencyOrder;
  }

  public void setAdjacencyOrder(List<String> adjacencyOrder) {
    this.adjacencyOrder = adjacencyOrder;
  }

  public List<Integer> getSites() {
    return sites;
  }

  public void setSites(List<Integer> sites) {
    this.sites = sites;
  }

  public List<List<Double>> getSiteCoordinates() {
    return siteCoordinates;
  }

  public void setSiteCoordinates(List<List<Double>> siteCoordinates) {
    this.siteCoordinates = siteCoordinates;
  }

  public List<RatioResults> getRatios() {
    return ratios;
  }

  public void setRatios(List<RatioResults> ratios) {
    this.ratios = ratios;
  }

}
