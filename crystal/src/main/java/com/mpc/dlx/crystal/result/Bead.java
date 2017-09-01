package com.mpc.dlx.crystal.result;

import java.util.List;

public class Bead {

  private int id;
  private int siteId;
  private List<Coordinate> coordinates;

  public Bead() {
  }

  public Bead(int id, int siteId) {
    this.id = id;
    this.siteId = siteId;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getSiteId() {
    return siteId;
  }

  public void setSiteId(int siteId) {
    this.siteId = siteId;
  }

  public List<Coordinate> getCoordinates() {
    return coordinates;
  }

  public void setCoordinates(List<Coordinate> coordinates) {
    this.coordinates = coordinates;
  }
}
