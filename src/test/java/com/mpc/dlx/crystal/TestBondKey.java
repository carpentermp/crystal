package com.mpc.dlx.crystal;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestBondKey {

  @Test
  public void testBondKey() {
    BondKey key1 = new BondKey(1, Direction.Right, 2);
    BondKey key2 = new BondKey(2, Direction.Right, 1);
    BondKey key3 = new BondKey(2, Direction.Left, 1);

    assertEquals(1, key1.getFromNodeId());
    assertEquals(Direction.Right, key1.getDirection());
    assertEquals(2, key1.getToNodeId());

    assertEquals(1, key2.getFromNodeId());
    assertEquals(Direction.Left, key2.getDirection());
    assertEquals(2, key2.getToNodeId());

    assertEquals(1, key3.getFromNodeId());
    assertEquals(Direction.Right, key3.getDirection());
    assertEquals(2, key3.getToNodeId());

    assertEquals(key1, key3);
    assertNotEquals(key1, key2);
    assertEquals(key1.hashCode(), key3.hashCode());
    assertNotEquals(key1.hashCode(), key2.hashCode());
  }

}
