package com.mpc.dlx.crystal;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TestCoordinate {

  @Test
  public void testCoordinate() {
    Coordinate coordinate = new Coordinate(1.0, 2.0, 3.0);
    List<Double> list = coordinate.toList();
    assertEquals(1.0, list.get(0), 0.1);
    assertEquals(2.0, list.get(1), 0.1);
    assertEquals(3.0, list.get(2), 0.1);
  }

}
