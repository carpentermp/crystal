package com.mpc.dlx.crystal;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;

public class TestCrystalResult {

  private Crystal c1372;
  private Row row;

  @Before
  public void setUp() {
    c1372 = new Crystal(Utils.getResourceFilename("1372"));
    row = new Row(2820, Molecule.m05, Molecule.m05.getUsedNodeIds(c1372.getNode(2820)));
  }

  @Test
  public void testBuildAdjacencyName() {
    assertEquals("1-1", CrystalResult.buildAdjacencyName(1, 1));
    assertEquals("1-3", CrystalResult.buildAdjacencyName(3, 1));
  }

  @Test
  public void testComputeAdjacencyOrder() {
    assertEquals("1-1, 1-2, 1-3, 1-4, 1-5, 2-2, 2-3, 2-4, 2-5, 3-3, 3-4, 3-5, 4-4, 4-5, 5-5",
        Utils.join(CrystalResult.computeAdjacencyOrder(Molecule.m05), ", "));
  }

  @Test
  public void testBuildNodeToBeadIdMap() {
    Map<Node, Integer> map = CrystalResult.buildNodeToBeadIdMap(c1372, Collections.singletonList(row));
    assertEquals(1, map.get(c1372.getNode(2820)).intValue());
    assertEquals(2, map.get(c1372.getNode(2582)).intValue());
    assertEquals(3, map.get(c1372.getNode(2622)).intValue());
    assertEquals(4, map.get(c1372.getNode(1261)).intValue());
    assertEquals(5, map.get(c1372.getNode(1063)).intValue());
  }

  @Test
  public void testBuildAdjacencyCountMap() {
    Map<String, Integer> map = CrystalResult.buildAdjacencyCountMap(c1372, Collections.singletonList(row));
    assertEquals(1, map.get("1-2").intValue());
    assertEquals(1, map.get("1-4").intValue());
    assertEquals(1, map.get("2-3").intValue());
    assertEquals(1, map.get("2-4").intValue());
    assertEquals(1, map.get("3-5").intValue());
    assertNull(map.get("1-1"));
    assertNull(map.get("1-3"));
    assertNull(map.get("2-2"));
    assertNull(map.get("4-4"));
    assertNull(map.get("5-5"));
  }

}
