package org.styrse.Algorithmic.BackTracking.Maze;

public class Main {

  public static void main(String[] args) {
    int[][] maze = {
      {1, 0, 1, 1, 1, 0},
      {1, 1, 1, 0, 1, 0},
      {0, 0, 1, 0, 1, 1},
      {0, 1, 1, 1, 0, 1},
      {0, 1, 0, 0, 0, 1},
      {0, 1, 1, 1, 1, 1}
    };

    int[][] path = MazeSolver.solveMaze(maze);
    if (path == null) {
      System.out.println("Ingen løsning fundet.");
    } else {
      MazeSolver.printPath(path);
    }
  }
}

