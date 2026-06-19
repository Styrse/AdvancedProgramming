package org.styrse.Sorting;

public class Sorter {
  private SortStrategy strategy;

  public Sorter(SortStrategy strategy) {
    this.strategy = strategy;
  }

  public void setStrategy(SortStrategy strategy) {
    this.strategy = strategy;
  }

  public int[] sort(int[] array) {
    return strategy.sort(array);
  }
}
