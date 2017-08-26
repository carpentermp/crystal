package com.mpc.dlx.crystal;

@SuppressWarnings("WeakerAccess")
public enum Orientation {
  Left,
  Right;

  public Orientation opposite() {
    return this == Left ? Right : Left;
  }
}
