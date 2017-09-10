package com.mpc.dlx.crystal;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestCrystal {

  private Crystal c1372;
  private Crystal c59;

  @Before
  public void setUp() {
    c1372 = new Crystal(Utils.getResourceFilename("1372"));
    c59 = new Crystal(Utils.getResourceFilename("59"));
  }

  @Test
  public void testC1372() {
    assertEquals(60, c1372.size());
    for (Integer nodeId : c1372.getNodeIds()) {
      assertNotNull(c1372.getNode(nodeId));
    }
    assertNull(c1372.getNode(0));
    assertEquals("c1372", c1372.getName());
    assertEquals(9, c1372.getCoordinates(2820).size());
  }

  @Test
  public void testC59() {
    assertEquals(12, c59.size());
    for (Integer nodeId : c59.getNodeIds()) {
      assertNotNull(c59.getNode(nodeId));
    }
    assertNull(c59.getNode(0));
    assertEquals("c59", c59.getName());
    assertEquals(9, c59.getCoordinates(2421).size());
  }

  @Test
  public void testNameFromBaseDir() {
    assertEquals("c59", Crystal.nameFromBaseDir("/a/b/c/59/"));
    assertEquals("c59", Crystal.nameFromBaseDir("/a/b/c/59"));
  }

}
