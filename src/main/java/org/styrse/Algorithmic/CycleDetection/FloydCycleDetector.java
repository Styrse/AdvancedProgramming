package org.styrse.Algorithmic.CycleDetection;

public class FloydCycleDetector {
  private FloydCycleDetector() {}

  public static boolean hasCycle(Node head) {
    Node slow = head;
    Node fast = head;

    while (fast != null && fast.next != null) {
      // Call next nodes to "move"
      slow = slow.next;
      fast = fast.next.next;
      if (slow == fast) return true;
    }
    return false;
  }
}

