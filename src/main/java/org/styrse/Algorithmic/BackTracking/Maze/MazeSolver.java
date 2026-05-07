package org.styrse.Algorithmic.BackTracking.Maze;

public class MazeSolver {
    // Wrapper der starter fra (0,0) og returnerer den fundne sti (eller null hvis ingen findes)
    public static int[][] solveMaze(int[][] maze) {
        int[][] path = new int[maze.length][maze[0].length];
        boolean solved = solveMaze(maze, 0, 0, path);
        return solved ? path : null;
    }

    // Backtracking: prøv at gå fra (row,col) til målet (felt med værdien 3)
    static boolean solveMaze(int[][] maze, int row, int col, int[][] path) {
        // 1) udenfor labyrinten?
        if (row < 0 || col < 0 || row >= maze.length || col >= maze[0].length) return false;

        // 2) mur
        if (maze[row][col] == 0) return false;

        // 3) allerede besøgt
        if (path[row][col] == 1) return false;

        // 4) marker feltet som en del af stien
        path[row][col] = 1;

        // 5) er vi i mål?
        if (maze[row][col] == 3) return true;

        // 6) prøver de fire retninger (højre, ned, venstre, op)
        if (solveMaze(maze, row, col + 1, path)) return true; // højre
        if (solveMaze(maze, row + 1, col, path)) return true; // ned
        if (solveMaze(maze, row, col - 1, path)) return true; // venstre
        if (solveMaze(maze, row - 1, col, path)) return true; // op

        // 7) ingen muligheder virkede -> backtrack
        path[row][col] = 0;
        return false;
    }

    public static void printPath(int[][] path) {
        for (int r = 0; r < path.length; r++) {
            for (int c = 0; c < path[0].length; c++) {
                System.out.print(path[r][c] + " ");
            }
            System.out.println();
        }
    }
}

