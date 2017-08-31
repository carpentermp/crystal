package com.mpc.dlx.crystal;

@SuppressWarnings("WeakerAccess")
public enum Orientation {
  Left,
  Right,
  AChiral,
  Symmetric;

  public Orientation opposite() {
    switch (this) {
      case Left: return Right;
      case Right: return Left;
      default: return this;
    }
  }
}
