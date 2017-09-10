package com.mpc.dlx.crystal;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class TestUtils {

  @Test
  public void testGetResourceFilename() {
    assertNotNull(Utils.getResourceFilename("1372"));
  }

  @Test
  public void testAddTrailingSlash() {
    assertEquals("abc/", Utils.addTrailingSlash("abc"));
    assertEquals("abc/", Utils.addTrailingSlash("abc/"));
  }

  @Test
  public void testJoin() {
    assertEquals("1, 2, 3", Utils.join(Arrays.asList(1, 2, 3), ", "));
  }

}
