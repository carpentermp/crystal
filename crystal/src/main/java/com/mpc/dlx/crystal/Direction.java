package com.mpc.dlx.crystal;

@SuppressWarnings("WeakerAccess")
public enum Direction {

  UpLeft,
  UpRight,
  Left,
  Right,
  DownLeft,
  DownRight;

  public static Direction[] rotate(Direction[] moves) {
    Direction[] rotatedMoves = new Direction[moves.length];
    for (int i = 0; i < moves.length; i++) {
      rotatedMoves[i] = moves[i].rotate();
    }
    return rotatedMoves;
  }

  public Direction opposite() {
    return rotate().rotate().rotate();
  }

  public Direction rotate() {
    switch (this) {
      case UpLeft: return UpRight;
      case UpRight: return Right;
      case Right: return DownRight;
      case DownRight: return DownLeft;
      case DownLeft: return Left;
      case Left: return UpLeft;
      default:
        throw new UnsupportedOperationException("Unknown move type!");
    }
  }

  public Direction mirror(Direction axis) {
    if (isAlongAxis(axis)) {
      return this;
    }
    Direction rotated = this.rotate();
    if (rotated.isAlongAxis(axis)) {
      return rotated.rotate();
    }
    return rotated.rotate().rotate().rotate();
  }

  private boolean isAlongAxis(Direction axis) {
    return this == axis || this.opposite() == axis;
  }

}
