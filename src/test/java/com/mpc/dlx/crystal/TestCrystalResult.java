package com.mpc.dlx.crystal;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class TestCrystalResult {

  private Crystal c554;
  private Row row;
  private Row row2;
  private Row dimerRow;

  @Before
  public void setUp() {
    c554 = new Crystal(Utils.getResourceFilename("554"), 5);
    row = new Row(2423, Molecule.m05, Molecule.m05.getUsedNodeIds(c554.getNode(2423)));
    Molecule mirror = Molecule.m05.mirror(Direction.Left);
    row2 = new Row(2423, mirror, mirror.getUsedNodeIds(c554.getNode(2423)));
    dimerRow = new Row(2423, Molecule.dimer, Molecule.dimer.getUsedNodeIds(c554.getNode(2423)));
  }

  @Test
  public void testGetBeadId() {
    RootMolecules rootMolecules = new RootMolecules(Molecule.m05, null, true);
    CrystalResult result = new CrystalResult(c554, rootMolecules, Collections.singletonList(row));
    assertEquals(1, result.getBeadId(2423));
    assertEquals(2, result.getBeadId(2463));
    assertEquals(3, result.getBeadId(2503));
    assertEquals(4, result.getBeadId(864));
    assertEquals(5, result.getBeadId(944));

    CrystalResult result2 = new CrystalResult(c554, rootMolecules, Collections.singletonList(row2));
    assertEquals(6, result2.getBeadId(2423));
    assertEquals(7, result2.getBeadId(2463));
    assertEquals(8, result2.getBeadId(2503));
    assertEquals(9, result2.getBeadId(863));
    assertEquals(10, result2.getBeadId(943));
  }

  @Test
  public void testGetBeadIdForDimer() {
    RootMolecules rootMolecules = new RootMolecules(Molecule.dimer, null, false);
    CrystalResult result = new CrystalResult(c554, rootMolecules, Collections.singletonList(dimerRow));
    assertEquals(1, result.getBeadId(2423));
    assertEquals(2, result.getBeadId(2463));
  }

  @Test
  public void testBuildAdjacencyCountMap() {
    RootMolecules rootMolecules = new RootMolecules(Molecule.m05, null, true);
    CrystalResult result = new CrystalResult(c554, rootMolecules, Collections.singletonList(row));
    List<Integer> adjacencyCounts = result.getAdjacencyCounts();
    assertEquals(15, adjacencyCounts.size());
    assertEquals(0, (int) adjacencyCounts.get(0)); // 1-1
    assertEquals(0, (int) adjacencyCounts.get(1)); // 1-2
    assertEquals(0, (int) adjacencyCounts.get(2)); // 1-3
    assertEquals(0, (int) adjacencyCounts.get(3)); // 1-4
    assertEquals(0, (int) adjacencyCounts.get(4)); // 1-5
    assertEquals(0, (int) adjacencyCounts.get(5)); // 2-2
    assertEquals(0, (int) adjacencyCounts.get(6)); // 2-3
    assertEquals(0, (int) adjacencyCounts.get(7)); // 2-4
    assertEquals(0, (int) adjacencyCounts.get(8)); // 2-5
    assertEquals(0, (int) adjacencyCounts.get(9)); // 3-3
    assertEquals(0, (int) adjacencyCounts.get(10)); // 3-4
    assertEquals(0, (int) adjacencyCounts.get(11)); // 3-5
    assertEquals(0, (int) adjacencyCounts.get(12)); // 4-4
    assertEquals(0, (int) adjacencyCounts.get(13)); // 4-5
    assertEquals(0, (int) adjacencyCounts.get(14)); // 5-5
  }

  @Test
  public void testBuildAdjacencyCountMapForDimer() {
    RootMolecules rootMolecules = new RootMolecules(Molecule.dimer, null, false);
    CrystalResult result = new CrystalResult(c554, rootMolecules, Collections.singletonList(dimerRow));
    List<Integer> adjacencyCounts = result.getAdjacencyCounts();
    assertEquals(3, adjacencyCounts.size());
    assertEquals(0, (int) adjacencyCounts.get(0)); // 1-1
    assertEquals(0, (int) adjacencyCounts.get(1)); // 1-2
    assertEquals(0, (int) adjacencyCounts.get(2)); // 2-2
  }

}
