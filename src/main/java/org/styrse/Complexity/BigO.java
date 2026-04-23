package org.styrse.Complexity;

public class BigO {
  public static void main(String[] args) {
    int n = 10000; // Juster n for at se effekten

    System.out.println("O(1) - Konstant tid:");
    System.out.println(constantSteps(n));

    System.out.println("\nO(log n) - Logaritmisk tid:");
    System.out.println(logSteps(n));

    System.out.println("\nO(n) - Lineær tid:");
    System.out.println(linearSteps(n));
  }

  public static int constantSteps(int n) {
    return 1;
  }

  public static int logSteps(int n) {
    int steps = 0;

    for (int i = n; i > 1; i /= 2) {
      steps ++;
    }

    return steps;
  }

  public static int linearSteps(int n) {
    int steps = 0;

    for (int i = 0; i < n; i++) {
      steps ++;
    }

    return steps;
  }
}
