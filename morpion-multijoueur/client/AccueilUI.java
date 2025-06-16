package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AccueilUI extends JDialog {
    private String pseudo = null;
    private char symbole = 'X';
    private boolean validated = false;

    public AccueilUI(JFrame parent) {
        super(parent, "Accueil Morpion", true);
        setLayout(new GridLayout(4, 1, 10, 10));
        setSize(300, 200);
        setLocationRelativeTo(parent);

        JTextField pseudoField = new JTextField();
        JPanel pseudoPanel = new JPanel(new BorderLayout());
        pseudoPanel.add(new JLabel("Pseudo : "), BorderLayout.WEST);
        pseudoPanel.add(pseudoField, BorderLayout.CENTER);
        add(pseudoPanel);

        JPanel symbolePanel = new JPanel();
        ButtonGroup group = new ButtonGroup();
        JRadioButton xBtn = new JRadioButton("X", true);
        JRadioButton oBtn = new JRadioButton("O");
        group.add(xBtn); group.add(oBtn);
        symbolePanel.add(new JLabel("Symbole : "));
        symbolePanel.add(xBtn); symbolePanel.add(oBtn);
        add(symbolePanel);

        JButton valider = new JButton("Valider");
        valider.addActionListener(e -> {
            String p = pseudoField.getText().trim();
            if (p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Merci d'entrer un pseudo.");
                return;
            }
            pseudo = p;
            symbole = xBtn.isSelected() ? 'X' : 'O';
            validated = true;
            setVisible(false);
        });
        add(valider);
    }

    public String getPseudo() { return pseudo; }
    public char getSymbole() { return symbole; }
    public boolean isValidated() { return validated; }

    public void setSymbole(char symbole) {
        this.symbole = symbole;
        // Met à jour les boutons radio si besoin
        // (à implémenter si tu veux rendre l'UI dynamique)
    }

    public static AccueilUI showDialog(JFrame parent) {
        AccueilUI dialog = new AccueilUI(parent);
        dialog.setVisible(true);
        return dialog;
    }
}
