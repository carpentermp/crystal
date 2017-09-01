package com.mpc.dlx.crystal;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestUtils {

  @Test
  public void testGetResourceFilename() {
    assertNotNull(Utils.getResourceDirectory("neighbors.txt"));
  }

}
