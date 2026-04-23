package org.styrse.Complexity;

import java.util.ArrayList;
import java.util.HashSet;

public class SearchPerformance {

  public static void main(String[] args) {
    int size = 500000;

    ArrayList<Integer> arrayList = new ArrayList<>();
    HashSet<Integer> hashSet = new HashSet<>();

    for (int i = 0; i < size; i++) {
      arrayList.add(i);
      hashSet.add(i);
    }

    // Element der findes
    long start = System.nanoTime();
    arrayList.contains(250000);
    long stop = System.nanoTime();
    System.out.println("ArrayList contains(250000): " + (stop - start) + " ns");

    start = System.nanoTime();
    hashSet.contains(250000);
    stop = System.nanoTime();
    System.out.println("HashSet contains(250000): " + (stop - start) + " ns");

    System.out.println();

    // Element der ikke findes
    start = System.nanoTime();
    arrayList.contains(600000);
    stop = System.nanoTime();
    System.out.println("ArrayList contains(600000): " + (stop - start) + " ns");

    start = System.nanoTime();
    hashSet.contains(600000);
    stop = System.nanoTime();
    System.out.println("HashSet contains(600000): " + (stop - start) + " ns");
  }
}
