package com.mpc.dlx.crystal;

@SuppressWarnings("WeakerAccess")
public enum Orientation {
  Left,
  Right,
  AChiral,
  Symmetric;

  public Orientation opposite() {
    return this == Left ? Right : Left;
  }
}
