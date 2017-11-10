package com.mpc.dlx.crystal;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class TestCrystalResult {

  private Crystal c1372;
  private Row row;
  private Row row2;

  @Before
  public void setUp() {
    c1372 = new Crystal(Utils.getResourceFilename("1372"));
    row = new Row(2820, Molecule.m05, Molecule.m05.getUsedNodeIds(c1372.getNode(2820)));
    Molecule mirror = Molecule.m05.mirror(Direction.Left);
    row2 = new Row(2820, mirror, mirror.getUsedNodeIds(c1372.getNode(2820)));
  }

  @Test
  public void testBuildNodeToBeadIdMap() {
    Map<Node, Integer> map = CrystalResult.buildNodeToBeadIdMap(c1372, Collections.singletonList(row), false);
    assertEquals(1, map.get(c1372.getNode(2820)).intValue());
    assertEquals(2, map.get(c1372.getNode(2582)).intValue());
    assertEquals(3, map.get(c1372.getNode(2622)).intValue());
    assertEquals(4, map.get(c1372.getNode(1261)).intValue());
    assertEquals(5, map.get(c1372.getNode(1063)).intValue());
    map = CrystalResult.buildNodeToBeadIdMap(c1372, Collections.singletonList(row2), true);
    assertEquals(6, map.get(c1372.getNode(2820)).intValue());
    assertEquals(7, map.get(c1372.getNode(2582)).intValue());
    assertEquals(8, map.get(c1372.getNode(2622)).intValue());
    assertEquals(9, map.get(c1372.getNode(982)).intValue());
    assertEquals(10, map.get(c1372.getNode(1062)).intValue());
  }

  @Test
  public void testBuildAdjacencyCountMap() {
    List<Row> rows = Collections.singletonList(row);
    Map<String, Integer> map = CrystalResult.buildAdjacencyCountMap(CrystalResult.buildNodeToBeadIdMap(c1372, rows, false));
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
