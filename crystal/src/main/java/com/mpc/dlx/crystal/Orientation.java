package com.mpc.dlx.crystal;

@SuppressWarnings({"WeakerAccess", "squid:S00115"})
public enum Orientation {
  Left,
  Right,
  AChiral,   // used for molecules that don't have a left and right side but still need 6 rotations
  Symmetric, // used for molecules that only need 3 rotations
  Circular;  // used for holes

  public Orientation opposite() {
    switch (this) {
      case Left: return Right;
      case Right: return Left;
      default: return this;
    }
  }
}
