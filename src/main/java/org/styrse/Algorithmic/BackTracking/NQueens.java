package org.styrse.Algorithmic.BackTracking;

import java.util.Arrays;

public class NQueens {

    static final int N = 4;

    static char[][] board = new char[N][N];

    static{
        for (int i = 0; i < N; i++) {
            Arrays.fill(board[i], '.');
        }

    }

    // Vi går ud fra, at vi starter med at placere dronning i række 0 og slutter i række N
    // Hvis vi forsøger at placere i række N skal der derfor printes og returneres.
    static void placeQueen(int row) {
        if (row == N) {
            printBoard();
            return;
        }


        for (int col = 0; col < N; col++) {
            if (isSafe(row, col)) {
                board[row][col] = 'Q';
                placeQueen(row + 1);
                board[row][col] = '.'; // backtrack, fjern dronningen
            }
        }
    }

    static boolean isSafe(int row, int col) {
        // Tjek kolonne og diagonaler (ikke rækker, da vi tager det række for række)
        // Vi behøver kun tjekke kolonner og diagonaler over denne række (i < row) da vi arbejder os nedad
        for (int i = 0; i < row; i++) {
            // Kolonnen opad
            if (board[i][col] == 'Q') return false;
            // Venstre diagonal opad
            if (col - (row - i) >= 0 && board[i][col - (row - i)] == 'Q') return false;
            // Højre diagonal opad
            if (col + (row - i) < N && board[i][col + (row - i)] == 'Q') return false;
        }
        return true;
    }

    static void printBoard() {
        for (char[] row : board) {
            System.out.println(new String(row));
        }
        System.out.println();
    }

}
