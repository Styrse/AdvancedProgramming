package org.styrse.Sorting;

public class BubbleSortStrategy implements SortStrategy {
  @Override
  public int[] sort(int[] array) {
    return Main.bubbleSort(array);
  }
}
