package com.mpc.dlx.crystal;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestDirection {

  @Test
  public void testRotate() {
    Direction[] moves = new Direction[]{Direction.UpLeft, Direction.UpRight, Direction.Right, Direction.DownRight, Direction.DownLeft, Direction.Left};
    Direction[] rotatedMoves = Direction.rotate(moves);
    assertEquals(Direction.UpRight, rotatedMoves[0]);
    assertEquals(Direction.Right, rotatedMoves[1]);
    assertEquals(Direction.DownRight, rotatedMoves[2]);
    assertEquals(Direction.DownLeft, rotatedMoves[3]);
    assertEquals(Direction.Left, rotatedMoves[4]);
    assertEquals(Direction.UpLeft, rotatedMoves[5]);
    assertEquals(Direction.Back, Direction.Back.rotate());
  }

  @Test
  public void testOpposite() {
    assertEquals(Direction.UpRight, Direction.DownLeft.opposite());
    assertEquals(Direction.Right, Direction.Left.opposite());
    assertEquals(Direction.DownRight, Direction.UpLeft.opposite());
    assertEquals(Direction.DownLeft, Direction.UpRight.opposite());
    assertEquals(Direction.Left, Direction.Right.opposite());
    assertEquals(Direction.UpLeft, Direction.DownRight.opposite());
  }

  @Test
  public void testMirror() {
    assertEquals(Direction.UpRight, Direction.DownRight.mirror(Direction.Left));
    assertEquals(Direction.UpLeft, Direction.DownLeft.mirror(Direction.Left));
    assertEquals(Direction.DownRight, Direction.UpRight.mirror(Direction.Left));
    assertEquals(Direction.DownLeft, Direction.UpLeft.mirror(Direction.Left));
    assertEquals(Direction.Left, Direction.Left.mirror(Direction.Left));
    assertEquals(Direction.Right, Direction.Right.mirror(Direction.Left));
    assertEquals(Direction.UpRight, Direction.DownRight.mirror(Direction.Right));
    assertEquals(Direction.UpLeft, Direction.DownLeft.mirror(Direction.Right));
    assertEquals(Direction.DownRight, Direction.UpRight.mirror(Direction.Right));
    assertEquals(Direction.DownLeft, Direction.UpLeft.mirror(Direction.Right));
    assertEquals(Direction.Left, Direction.Left.mirror(Direction.Right));
    assertEquals(Direction.Right, Direction.Right.mirror(Direction.Right));

    assertEquals(Direction.UpRight, Direction.Left.mirror(Direction.UpLeft));
    assertEquals(Direction.UpLeft, Direction.UpLeft.mirror(Direction.UpLeft));
    assertEquals(Direction.DownRight, Direction.DownRight.mirror(Direction.UpLeft));
    assertEquals(Direction.DownLeft, Direction.Right.mirror(Direction.UpLeft));
    assertEquals(Direction.Left, Direction.UpRight.mirror(Direction.UpLeft));
    assertEquals(Direction.Right, Direction.DownLeft.mirror(Direction.UpLeft));
    assertEquals(Direction.UpRight, Direction.Left.mirror(Direction.DownRight));
    assertEquals(Direction.UpLeft, Direction.UpLeft.mirror(Direction.DownRight));
    assertEquals(Direction.DownRight, Direction.DownRight.mirror(Direction.DownRight));
    assertEquals(Direction.DownLeft, Direction.Right.mirror(Direction.DownRight));
    assertEquals(Direction.Left, Direction.UpRight.mirror(Direction.DownRight));
    assertEquals(Direction.Right, Direction.DownLeft.mirror(Direction.DownRight));

  }

  @Test
  public void testFromValue() {
    assertEquals(Direction.Right, Direction.fromValue(1));
    assertEquals(Direction.DownRight, Direction.fromValue(2));
    assertEquals(Direction.DownLeft, Direction.fromValue(3));
    assertEquals(Direction.Left, Direction.fromValue(4));
    assertEquals(Direction.UpLeft, Direction.fromValue(5));
    assertEquals(Direction.UpRight, Direction.fromValue(6));
    try {
      Direction.fromValue(99);
      fail("should have failed with direction: 0");
    }
    catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testValue() {
    assertEquals(1, Direction.Right.value());
    assertEquals(2, Direction.DownRight.value());
    assertEquals(3, Direction.DownLeft.value());
    assertEquals(4, Direction.Left.value());
    assertEquals(5, Direction.UpLeft.value());
    assertEquals(6, Direction.UpRight.value());
    assertEquals(0, Direction.Back.value());
  }

}
