package common;

/** Un mouvement (ligne, colonne, joueur). */
public class Move {
    private final int row, col;
    private final char player;

    public Move(int row, int col, char player) {
        this.row = row;
        this.col = col;
        this.player = player;
    }

    public int getRow()     { return row; }
    public int getCol()     { return col; }
    public char getPlayer() { return player; }
}
