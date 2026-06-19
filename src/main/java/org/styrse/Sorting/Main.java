package org.styrse.Sorting;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
  public static void main(String[] args) {
    int[] intArray = randomUniqueArray();

    //Sorter sorter = new Sorter(new BubbleSortStrategy());
    //Sorter sorter = new Sorter(new MergeSortStrategy());
    Sorter sorter = new Sorter(new QuickSortStrategy());
    int[] sortedArray = sorter.sort(intArray);

    for (int i = 0; i < sortedArray.length; i++) {
      System.out.println(sortedArray[i]);
    }
  }

  public static int[] randomUniqueArray() {
    return randomUniqueIntArray(13, 0, 99);
  }

  public static int[] randomUniqueIntArray(int length, int minInclusive, int maxInclusive) {
    int[] numbers = new int[length];
    ThreadLocalRandom random = ThreadLocalRandom.current();

    for (int i = 0; i < length; i++) {
      int number;
      do {
        number = random.nextInt(minInclusive, maxInclusive + 1);
      } while (contains(numbers, i, number));

      numbers[i] = number;
    }

    return numbers;
  }

  private static boolean contains(int[] array, int length, int value) {
    for (int i = 0; i < length; i++) {
      if (array[i] == value) {
        return true;
      }
    }

    return false;
  }

  public static int[] bubbleSort(int[] array) {
    // "Grabbing" first index
    for (int i = 0; i < array.length - 1; i++) {
      for (int j = 0; j < array.length - 1 - i; j++) {
        // Compares it to the next index
        if (array[j] > array[j + 1]) {
          // Calls the method if value at the later index is lower
          swap(array, j, j + 1);
        }
      }
    }

    return array;
  }

  public static int[] mergeSort(int[] arrayToSort) {
    if (arrayToSort.length > 1) {
      int mid = arrayToSort.length / 2;

      int[] leftArray = Arrays.copyOfRange(arrayToSort, 0, mid);
      int[] rightArray = Arrays.copyOfRange(arrayToSort, mid, arrayToSort.length);

      mergeSort(leftArray);
      mergeSort(rightArray);

      merge(arrayToSort, leftArray, rightArray);
    }

    return arrayToSort;
  }

  public static int[] merge(int[] arrayToSort, int[] leftArray, int[] rightArray) {
    int i = 0, j = 0, k = 0;

    while (i < leftArray.length && j < rightArray.length) {
      if (leftArray[i] <= rightArray[j]) {
        arrayToSort[k] = leftArray[i];
        i++;
      } else {
        arrayToSort[k] = rightArray[j];
        j++;
      }
      k++;
    }

    while (i < leftArray.length) {
      arrayToSort[k] = leftArray[i];
      i++;
      k++;
    }

    while (j < rightArray.length) {
      arrayToSort[k] = rightArray[j];
      j++;
      k++;
    }

    return arrayToSort;
  }

  public static int[] quicksort(int[] arrayToSort) {
    return quicksort(arrayToSort, 0, arrayToSort.length - 1);
  }

  private static int[] quicksort(int[] arrayToSort, int low, int high) {
    if (low < high) {
      int pivotIndex = partition(arrayToSort, low, high);
      quicksort(arrayToSort, low, pivotIndex - 1);
      quicksort(arrayToSort, pivotIndex + 1, high);
    }

    return arrayToSort;
  }

  public static int partition(int[] arrayToSort, int low, int high) {
    int pivot = arrayToSort[high];
    int i = low - 1;

    for (int j = low; j < high; j++) {
      if (arrayToSort[j] <= pivot) {
        i++;

        swap(arrayToSort, i, j);
      }
    }

    swap(arrayToSort, i + 1, high);

    return i + 1;
  }

  private static void swap(int[] arr, int i, int j) {
    // Lader to indexes bytte plads ved at lave en midlertidig variabel
    int temp = arr[i];
    arr[i] = arr[j];
    arr[j] = temp;
  }
}
