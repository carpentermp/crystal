package com.mpc.dlx.crystal;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestNode {

  @Test
  public void testSetGet() {
    Node node = new Node(0);
    assertEquals(0, node.getId());
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

  @Test
  public void testEquals() {
    Node n1 = new Node(1);
    Node n2 = new Node(2);
    Node n3 = new Node(1);
    assertNotEquals(n1, n2);
    assertNotEquals(n2, n3);
    assertEquals(n1, n3);
  }

  @Test
  public void testHashCode() {
    Node n1 = new Node(1);
    Node n2 = new Node(2);
    Node n3 = new Node(1);
    assertNotEquals(n1.hashCode(), n2.hashCode());
    assertNotEquals(n2.hashCode(), n3.hashCode());
    assertEquals(n1.hashCode(), n3.hashCode());
  }

}
