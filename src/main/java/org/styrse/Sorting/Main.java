package org.styrse.Sorting;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Main {
  public static void main(String[] args) {
    int[] intArray = randomUniqueArray();

    /*
    for (int i = 0; i < intArray.length; i++) {
      System.out.println(intArray[i]);
    }

    System.out.println("");

    int[] sortedArray = mergeSort(intArray);

    for (int i = 0; i < sortedArray.length; i++) {
      System.out.println(sortedArray[i]);
    }
     */

    int[] quicksortedArray = quicksort(intArray);

    for (int i = 0; i < quicksortedArray.length; i++) {
      System.out.println(quicksortedArray[i]);
    }

  }

  public static int[] randomUniqueArray() {
    return randomUniqueIntArray(13, 0, 99);
  }

  public static int[] randomUniqueIntArray(int length, int minInclusive, int maxInclusive) {
    int rangeSize = (maxInclusive - minInclusive) + 1;
    if (rangeSize < length) {
      throw new IllegalArgumentException("Intervallet er for lille til at lave " + length + " unikke tal");
    }

    int[] pool = new int[rangeSize];
    for (int i = 0; i < rangeSize; i++) {
      pool[i] = minInclusive + i;
    }

    ThreadLocalRandom random = ThreadLocalRandom.current();
    for (int i = 0; i < length; i++) {
      int j = random.nextInt(i, rangeSize);
      int temp = pool[i];
      pool[i] = pool[j];
      pool[j] = temp;
    }

    return Arrays.copyOf(pool, length);
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

  private static int[] quicksort(int[] arrayToSort) {
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
