package org.styrse.Algorithmic.CycleDetection;

public class ListFactory {
  // Bygger en liste af int-værdier
  public static Node buildList(int... values) {
    if (values.length == 0) return null;
    Node head = new Node(values[0]);
    Node current = head;
    for (int i = 1; i < values.length; i++) {
      current.next = new Node(values[i]);
      current = current.next;
    }
    return head;
  }

  // Vi laver en cyklisk liste
  public static Node buildListWithCycle() {
    Node n1 = new Node(1);
    Node n2 = new Node(2);
    Node n3 = new Node(3);
    Node n4 = new Node(4);
    Node n5 = new Node(5);
    n1.next = n2;
    n2.next = n3;
    n3.next = n4;
    n4.next = n5;
    n5.next = n2; // Laver cyklus
    return n1;
  }
}
