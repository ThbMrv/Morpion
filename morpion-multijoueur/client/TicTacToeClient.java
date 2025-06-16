package client;
import javax.swing.JOptionPane;

import common.Board;
import common.Message;

import java.io.*;
import java.net.Socket;

/**
 * Client Swing pour le jeu Morpion multijoueur.
 * Gère la connexion au serveur, l'envoi/réception des messages
 * et la synchronisation avec l'interface graphique.
 */
public class TicTacToeClient {
    private static final String HOST = "localhost";
    private static final int PORT = 5001;

    private volatile Board board = new Board();
    private volatile char myMark = '?';
    private volatile char currentTurn = 'X';
    private volatile boolean gameEnded = false;

    private String pseudo = "";
    private String adversaire = "";

    public static void main(String[] args) {
        try {
            new TicTacToeClient().startGui();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Erreur de connexion au serveur.", "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void startGui() throws IOException {
        // Fenêtre d'accueil
        ClientSwingUI ui = new ClientSwingUI();
        AccueilUI accueil = AccueilUI.showDialog(ui);
        if (!accueil.isValidated()) System.exit(0);

        pseudo = accueil.getPseudo();
        char symboleChoisi = accueil.getSymbole();
        ui.setPseudo(pseudo);
        ui.setVisible(true);

        // Connexion au serveur
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println("PSEUDO:" + pseudo + ":" + symboleChoisi);

            // Thread pour écouter les messages du serveur
            new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        handleServerMessage(line, ui, out);
                    }
                } catch (IOException e) {
                    ui.showMessage("Connexion au serveur perdue.");
                    System.exit(0);
                }
            }).start();

            // Écoute des mouvements de l'utilisateur
            ui.setMoveListener((row, col) -> {
                if (myMark != '?' && currentTurn == myMark) {
                    out.println(Message.move(row, col));
                }
            });

            // Attente de la fin de partie
            while (!gameEnded) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) {}
            }
        }
    }

    private void handleServerMessage(String line, ClientSwingUI ui, PrintWriter out) {
        if (line.startsWith("START:")) {
            String[] parts = line.split(":");
            myMark = parts[1].charAt(0);
            if (parts.length > 2) {
                adversaire = parts[2];
                ui.setAdversaire(adversaire);
            }
            ui.setMyMark(myMark);
        }
        else if (line.startsWith("UPDATE:")) {
            String[] parts = line.split(":");
            board = Board.deserialize(parts[1]);
            currentTurn = parts[2].charAt(0);
            ui.updateBoard(board, currentTurn);
        }
        else if (line.startsWith("END:")) {
            handleGameEnd(line.substring(4), ui);
        }
        else if (line.startsWith("ERROR:")) {
            ui.showMessage("Erreur : " + line.substring(6));
        }
    }

    private void handleGameEnd(String result, ClientSwingUI ui) {
        gameEnded = true;
        String message;

        if (result.startsWith("WIN:")) {
            char winner = result.charAt(4);
            String winnerName = (winner == myMark) ? pseudo : adversaire;
            String loserName = (winner == myMark) ? adversaire : pseudo;
            message = "🏆 Victoire de : " + winnerName + "\n❌ Défaite de : " + loserName;
        } else if (result.equals("DRAW")) {
            message = "Match nul entre " + pseudo + (adversaire.isEmpty() ? "" : " et " + adversaire);
        } else {
            message = "Partie terminée : " + result;
        }

        ui.updateBoard(board, '-');
        ui.showMessage(message);
        System.exit(0);
    }
}
