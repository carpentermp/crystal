package com.mpc.dlx.crystal;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class TestCrystal {

  private Crystal c59;

  @Before
  public void setUp() {
    c59 = new Crystal(Utils.getResourceFilename("59"), 5);
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
  public void testC150WithP3Symmetry() throws IOException {
    File d150 = new File(Utils.getResourceFilename("150"));
    File symmetryFile = new File(d150 + "/symmetry/p3.txt");
    Symmetry symmetry = new Symmetry(symmetryFile);
    Crystal c150 = new Crystal(d150, 5, symmetry);
    assertEquals(6, c150.getHoleCount());

  }

  @Test
  public void testNameFromBaseDir() {
    assertEquals("c59", Crystal.computeName(new File("/a/b/c/59/"), null));
    assertEquals("c59", Crystal.computeName(new File("/a/b/c/59"), null));
  }

}
