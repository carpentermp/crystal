package com.mpc.dlx.crystal.result;

public class Adjacency {

  private String name;
  private int count;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getCount() {
    return count;
  }

  public void setCount(int count) {
    this.count = count;
  }

  public int incrementCount() {
    return ++count;
  }

  public String toString() {
    return name + ": " + count;
  }

}
