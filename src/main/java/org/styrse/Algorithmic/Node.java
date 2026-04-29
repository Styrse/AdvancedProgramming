package org.styrse.Algorithmic;

public class Node {
  int value;
  Node next;

  Node(int value) {
    this.value = value;
  }

  public String toString() {
    String result = "" + value;
    if (next != null) {
      result += " -> " + next.toString();
    }
    return result;
  }
}
