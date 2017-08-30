package com.mpc.dlx.crystal;

@SuppressWarnings("WeakerAccess")
public enum Direction {

  UpLeft(5),
  UpRight(6),
  Left(4),
  Right(1),
  DownLeft(3),
  DownRight(2);

  private final int value;

  Direction(int value) {
    this.value = value;
  }

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

  public static Direction fromValue(int value) {
    for (Direction direction : Direction.values()) {
      if (direction.value == value) {
        return direction;
      }
    }
    throw new IllegalArgumentException("Invalid direction value: " + value);
  }

}
