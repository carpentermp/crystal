package com.mpc.dlx.crystal;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TestCrystal {

  private Crystal crystal;

  @Before
  public void setUp() {
    crystal = new Crystal(Utils.getResourceFilename("neighbors.txt"));
  }

  @Test
  public void testGetNode() {
    assertEquals(60, crystal.size());
    for (Integer nodeId : crystal.getNodeIds()) {
      assertNotNull(crystal.getNode(nodeId));
    }
    assertNull(crystal.getNode(0));
  }

}
