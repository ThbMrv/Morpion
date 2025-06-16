package client;

import javax.swing.*;
import java.awt.*;

public class AccueilUI extends JDialog {
    private String pseudo = null;
    private char symbole = 'X';
    private boolean validated = false;

    public AccueilUI(JFrame parent) {
        super(parent, "Bienvenue dans le Morpion", true);
        setSize(450, 330);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Thème clair et moderne
        Color bgColor = new Color(250, 250, 255);
        Color panelColor = new Color(255, 255, 255);
        Color accentColor = new Color(0, 120, 215);         // bleu Windows
        Color borderColor = new Color(230, 230, 230);
        Color textColor = new Color(30, 30, 30);

        Font titleFont = new Font("Segoe UI", Font.BOLD, 18);
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Titre
        JLabel title = new JLabel("Connexion au Morpion");
        title.setFont(titleFont);
        title.setForeground(accentColor);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(title);

        mainPanel.add(Box.createVerticalStrut(25));

        // Panel pseudo
        JPanel pseudoPanel = new JPanel(new BorderLayout(10, 10));
        pseudoPanel.setBackground(panelColor);
        pseudoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        JLabel pseudoLabel = new JLabel("Pseudo : ");
        pseudoLabel.setFont(labelFont);
        pseudoLabel.setForeground(textColor);
        JTextField pseudoField = new JTextField();
        pseudoField.setFont(fieldFont);
        pseudoField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pseudoField.setBackground(new Color(245, 245, 245));
        pseudoField.setForeground(textColor);
        pseudoPanel.add(pseudoLabel, BorderLayout.WEST);
        pseudoPanel.add(pseudoField, BorderLayout.CENTER);
        mainPanel.add(pseudoPanel);

        mainPanel.add(Box.createVerticalStrut(20));

        // Panel symbole
        JPanel symbolePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        symbolePanel.setBackground(panelColor);
        symbolePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(borderColor),
                "Choisissez votre symbole",
                0, 0,
                labelFont,
                accentColor
        ));
        JRadioButton xBtn = new JRadioButton("X");
        JRadioButton oBtn = new JRadioButton("O");
        ButtonGroup group = new ButtonGroup();
        group.add(xBtn);
        group.add(oBtn);
        xBtn.setSelected(true);
        styleRadio(xBtn, panelColor, textColor);
        styleRadio(oBtn, panelColor, textColor);
        symbolePanel.add(xBtn);
        symbolePanel.add(oBtn);
        mainPanel.add(symbolePanel);

        mainPanel.add(Box.createVerticalStrut(25));

        // Bouton valider
        JButton valider = new JButton("Commencer");
        valider.setBackground(accentColor);
        valider.setForeground(Color.BLACK);
        valider.setFont(new Font("Segoe UI", Font.BOLD, 15));
        valider.setFocusPainted(false);
        valider.setCursor(new Cursor(Cursor.HAND_CURSOR));
        valider.setAlignmentX(Component.CENTER_ALIGNMENT);

        valider.addActionListener(e -> {
            String p = pseudoField.getText().trim();
            if (p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un pseudo.", "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }
            pseudo = p;
            symbole = xBtn.isSelected() ? 'X' : 'O';
            validated = true;
            setVisible(false);
        });

        mainPanel.add(valider);
        add(mainPanel);
    }

    private void styleRadio(JRadioButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setFocusPainted(false);
    }

    public String getPseudo() { return pseudo; }
    public char getSymbole() { return symbole; }
    public boolean isValidated() { return validated; }

    public static AccueilUI showDialog(JFrame parent) {
        AccueilUI dialog = new AccueilUI(parent);
        dialog.setVisible(true);
        return dialog;
    }
}
