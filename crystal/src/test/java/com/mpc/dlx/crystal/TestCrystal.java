package com.mpc.dlx.crystal;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestCrystal {

  private Crystal crystal;

  @Before
  public void setUp() {
    crystal = new Crystal();
  }

  @Test
  public void testGetNode() {
    for (int i = 1; i <= 60; i++) {
      assertNotNull(crystal.getNode(i));
    }
    assertNull(crystal.getNode(0));
    assertNull(crystal.getNode(61));
  }

  @Test
  public void testLattice() {
    assertEquals(38, goToEnd(crystal.getNode(23), Direction.DownLeft).value());
    assertEquals(23, goToEnd(crystal.getNode(38), Direction.UpRight).value());
    assertEquals(45, goToEnd(crystal.getNode(16), Direction.Left).value());
    assertEquals(16, goToEnd(crystal.getNode(45), Direction.Right).value());
    assertEquals(60, goToEnd(crystal.getNode(1), Direction.DownRight).value());
    assertEquals(1, goToEnd(crystal.getNode(60), Direction.UpLeft).value());
  }

  @Test
  public void testTerminals() {
    assertNull(goToEnd(crystal.getNode(23), Direction.DownLeft).get(Direction.DownLeft));
    assertNull(goToEnd(crystal.getNode(38), Direction.UpRight).get(Direction.UpRight));
    assertNull(goToEnd(crystal.getNode(16), Direction.Left).get(Direction.Left));
    assertNull(goToEnd(crystal.getNode(45), Direction.Right).get(Direction.Right));
    assertNull(goToEnd(crystal.getNode(1), Direction.DownRight).get(Direction.DownRight));
    assertNull(goToEnd(crystal.getNode(60), Direction.UpLeft).get(Direction.UpLeft));
  }

  private Node goToEnd(Node node, Direction direction) {
    Node n = node;
    for (int i = 0; i < 59; i++) {
      n = n.get(direction);
    }
    return n;
  }

}
