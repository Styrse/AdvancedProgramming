package org.styrse.Sorting;

public class QuickSortStrategy implements SortStrategy {
  @Override
  public int[] sort(int[] array) {
    return Main.quicksort(array);
  }
}
