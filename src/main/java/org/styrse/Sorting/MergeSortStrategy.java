package org.styrse.Sorting;

public class MergeSortStrategy implements SortStrategy {
  @Override
  public int[] sort(int[] array) {
    return Main.mergeSort(array);
  }
}
