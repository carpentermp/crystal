package com.mpc.dlx.crystal.result;

public class Bead {

  private int id;
  private int siteId;
  private String x;
  private String y;
  private String z;

  public Bead() {
    // default constructor
  }

  public Bead(int id, int siteId, String x, String y, String z) {
    this.id = id;
    this.siteId = siteId;
    this.x = x;
    this.y = y;
    this.z = z;
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

  public String getX() {
    return x;
  }

  public void setX(String x) {
    this.x = x;
  }

  public String getY() {
    return y;
  }

  public void setY(String y) {
    this.y = y;
  }

  public String getZ() {
    return z;
  }

  public void setZ(String z) {
    this.z = z;
  }

}
