package client;

import common.Board;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientSwingUI extends JFrame {
    private final JButton[][] buttons = new JButton[3][3];
    private char myMark = '?';
    private char currentTurn = 'X';
    private Board board = new Board();
    private MoveListener moveListener;

    public interface MoveListener {
        void onMove(int row, int col);
    }

    public ClientSwingUI() {
        setTitle("Morpion");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 3));
        setSize(320, 340);
        setResizable(false);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                JButton btn = new JButton("");
                btn.setFont(new Font("Arial", Font.BOLD, 48));
                final int row = r, col = c;
                btn.addActionListener(e -> {
                    if (moveListener != null && btn.getText().equals("") && myMark == currentTurn) {
                        moveListener.onMove(row, col);
                    }
                });
                buttons[r][c] = btn;
                add(btn);
            }
        }
    }

    public void setMoveListener(MoveListener l) {
        this.moveListener = l;
    }

    public void updateBoard(Board b, char turn) {
        this.board = b;
        this.currentTurn = turn;
        String s = b.serialize();
        for (int i = 0; i < 9; i++) {
            int r = i / 3, c = i % 3;
            char ch = s.charAt(i);
            buttons[r][c].setText(ch == '-' ? "" : String.valueOf(ch));
        }
        setTitle("Morpion - "+(turn=='-'?"Fin de partie":("Tour de : "+turn))+" - Vous êtes "+myMark);
    }

    public void setMyMark(char mark) {
        this.myMark = mark;
        setTitle("Morpion - Vous êtes " + mark);
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
}
