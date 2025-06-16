package client;

import common.Board;

import javax.swing.*;
import java.awt.*;

public class ClientSwingUI extends JFrame {
    private final JButton[][] buttons = new JButton[3][3];
    private final JLabel infoLabel = new JLabel("Chargement...");
    private char myMark = '?';
    private char currentTurn = 'X';
    private Board board = new Board();
    private MoveListener moveListener;
    private String pseudo = "";
    private String adversaire = "";

    public interface MoveListener {
        void onMove(int row, int col);
    }

    public ClientSwingUI() {
        setTitle("Morpion - Connexion...");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(400, 450);
        setResizable(false);
        setLocationRelativeTo(null); // Centrage

        // Couleurs modernes
        Color bgColor = new Color(245, 248, 255);
        Color gridColor = new Color(220, 220, 235);
        Font btnFont = new Font("SansSerif", Font.BOLD, 42);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(bgColor);

        // En-tête
        infoLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.add(infoLabel, BorderLayout.NORTH);

        // Grille
        JPanel gridPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridPanel.setBackground(bgColor);

        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                JButton btn = new JButton();
                btn.setFont(btnFont);
                btn.setFocusPainted(false);
                btn.setBackground(Color.WHITE);
                btn.setForeground(new Color(50, 50, 50));
                btn.setBorder(BorderFactory.createLineBorder(gridColor, 2));

                final int row = r, col = c;
                btn.addActionListener(e -> {
                    if (moveListener != null && btn.getText().isEmpty() && myMark == currentTurn) {
                        moveListener.onMove(row, col);
                    }
                });

                buttons[r][c] = btn;
                gridPanel.add(btn);
            }
        }

        mainPanel.add(gridPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
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
        updateTitle(turn);
    }

    private void updateTitle(char turn) {
        String tour = (turn == '-') ? "Fin de partie" : ("Tour de : " + turn);
        String identite = pseudo + (!adversaire.isEmpty() ? " vs " + adversaire : "") + " — Vous êtes " + myMark;
        setTitle("Morpion - " + identite);
        infoLabel.setText(tour);
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
        updateTitle(currentTurn);
    }

    public void setAdversaire(String adv) {
        this.adversaire = adv;
        updateTitle(currentTurn);
    }

    public void setMyMark(char mark) {
        this.myMark = mark;
        updateTitle(currentTurn);
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }
}
