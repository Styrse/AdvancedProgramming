package org.styrse.Complexity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListPerformance {

  public static void main(String[] args) {
    int size = 500000;

    List<Integer> arrayList = new ArrayList<>();
    List<Integer> linkedList = new LinkedList<>();

    System.out.println("Fylder listerne...");
    for (int i = 0; i < size; i++) {
      arrayList.add(i);
      linkedList.add(i);
    }

    System.out.println("Lister klar.\n");

    testGet(arrayList, "ArrayList");
    testGet(linkedList, "LinkedList");

    System.out.println();

    testAdd(arrayList, "ArrayList");
    testAdd(linkedList, "LinkedList");

    System.out.println();

    testRemove(arrayList, "ArrayList");
    testRemove(linkedList, "LinkedList");
  }

  public static void testGet(List<Integer> list, String name) {
    long start = System.nanoTime();
    int value = list.get(250000);
    long stop = System.nanoTime();

    System.out.println(name + " get(250000): " + (stop - start) + " ns, value = " + value);
  }

  public static void testAdd(List<Integer> list, String name) {
    long start = System.nanoTime();
    list.add(250000, 99);
    long stop = System.nanoTime();

    System.out.println(name + " add(250000, 99): " + (stop - start) + " ns");
  }

  public static void testRemove(List<Integer> list, String name) {
    long start = System.nanoTime();
    list.remove(250000);
    long stop = System.nanoTime();

    System.out.println(name + " remove(250000): " + (stop - start) + " ns");
  }
}
