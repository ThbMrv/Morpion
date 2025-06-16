package common;

import java.util.Arrays;

/**
 * Plateau 3×3 et logique de victoire/égalité.
 */
public class Board {
    private final char[][] grid = new char[3][3];   // 'X', 'O' ou '\0'

    public Board() {
        for (char[] row : grid) Arrays.fill(row, '\0');
    }

    /** Coup autorisé ? */
    public synchronized boolean isMoveValid(int r, int c) {
        return r >= 0 && r < 3 && c >= 0 && c < 3 && grid[r][c] == '\0';
    }

    /** Applique un mouvement déjà validé. */
    public synchronized void applyMove(Move mv) {
        grid[mv.getRow()][mv.getCol()] = mv.getPlayer();
    }

    /**
     * @return 'X' ou 'O' si vainqueur, 'D' pour match nul, '\0' sinon.
     */
    public synchronized char checkWinner() {
        for (char p : new char[] { 'X', 'O' }) {
            // lignes & colonnes
            for (int i = 0; i < 3; i++) {
                if ((grid[i][0] == p && grid[i][1] == p && grid[i][2] == p) ||
                    (grid[0][i] == p && grid[1][i] == p && grid[2][i] == p))
                    return p;
            }
            // diagonales
            if ((grid[0][0] == p && grid[1][1] == p && grid[2][2] == p) ||
                (grid[0][2] == p && grid[1][1] == p && grid[2][0] == p))
                return p;
        }

        // grille pleine ?
        boolean full = true;
        for (char[] row : grid)
            for (char cell : row)
                if (cell == '\0') full = false;

        return full ? 'D' : '\0';
    }

    /** Sérialise la grille en 9 caractères (X, O ou -). */
    public synchronized String serialize() {
        StringBuilder sb = new StringBuilder(9);
        for (char[] row : grid)
            for (char c : row)
                sb.append(c == '\0' ? '-' : c);
        return sb.toString();
    }

    /** Construit un Board depuis la chaîne sérialisée. */
    public static Board deserialize(String s) {
        if (s == null || s.length() != 9) throw new IllegalArgumentException("Chaîne invalide");
        Board b = new Board();
        for (int i = 0; i < 9; i++) {
            char ch = s.charAt(i);
            if (ch == '-') ch = '\0';
            b.grid[i / 3][i % 3] = ch;
        }
        return b;
    }
}
