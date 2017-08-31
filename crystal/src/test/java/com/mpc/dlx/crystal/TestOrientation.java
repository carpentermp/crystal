package com.mpc.dlx.crystal;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestOrientation {

  @Test
  public void testOrientation() {
    assertEquals(Orientation.Right, Orientation.Left.opposite());
    assertEquals(Orientation.Left, Orientation.Right.opposite());
    assertEquals(Orientation.AChiral, Orientation.AChiral.opposite());
    assertEquals(Orientation.Symmetric, Orientation.Symmetric.opposite());
  }

}
