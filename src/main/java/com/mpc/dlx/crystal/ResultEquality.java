package com.mpc.dlx.crystal;

import java.util.Arrays;

@SuppressWarnings("WeakerAccess")
public class ResultEquality {

  private final byte[] theBytes;

  public ResultEquality(byte[] theBytes) {
    this.theBytes = theBytes;
  }

  public boolean equals(Object o) {
    return o instanceof ResultEquality && Arrays.equals(theBytes, ((ResultEquality) o).theBytes);
  }

  public int hashCode() {
    return Arrays.hashCode(theBytes);
  }

}
