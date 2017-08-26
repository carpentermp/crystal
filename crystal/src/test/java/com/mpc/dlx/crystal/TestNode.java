package com.mpc.dlx.crystal;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestNode {

  @Test
  public void testSetGet() {
    Node node = new Node(0);
    assertEquals(0, node.value());
    Node one = new Node(1);
    Node two = new Node(2);
    Node three = new Node(3);
    Node four = new Node(4);
    Node five = new Node(5);
    Node six = new Node(6);
    node.set(one, Direction.Right);
    node.set(two, Direction.DownRight);
    node.set(three, Direction.DownLeft);
    node.set(four, Direction.Left);
    node.set(five, Direction.UpLeft);
    node.set(six, Direction.UpRight);
    assertEquals(one, node.get(Direction.Right));
    assertEquals(two, node.get(Direction.DownRight));
    assertEquals(three, node.get(Direction.DownLeft));
    assertEquals(four, node.get(Direction.Left));
    assertEquals(five, node.get(Direction.UpLeft));
    assertEquals(six, node.get(Direction.UpRight));
  }

}
