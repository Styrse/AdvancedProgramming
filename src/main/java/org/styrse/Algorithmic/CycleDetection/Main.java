package org.styrse.Algorithmic.CycleDetection;

public class Main {
  public static void main(String[] args) {
    Node list = ListFactory.buildList(1, 2, 3, 4, 5);
    System.out.println(list);
    System.out.println("Has cycle: " + FloydCycleDetector.hasCycle(list));

    Node circularList = ListFactory.buildListWithCycle();
    System.out.println("Head: " + circularList.value);
    System.out.println("Has cycle: " + FloydCycleDetector.hasCycle(circularList));
  }
}
