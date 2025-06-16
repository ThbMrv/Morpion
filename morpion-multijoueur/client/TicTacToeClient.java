package client;

import common.Board;
import common.Message;
import common.Move;

import java.io.*;
import java.net.Socket;


/** Client graphique : envoie les coups et affiche le plateau dans une fenêtre Swing. */
public class TicTacToeClient {
    private static final String HOST = "localhost";
    private static final int PORT = 5001;

    private volatile Board board = new Board();
    private volatile char myMark = '?';
    private volatile char currentTurn = 'X';
    private volatile boolean gameEnded = false;
    private String pseudo = "";
    private String adversaire = "";

    public static void main(String[] args) throws IOException {
        new TicTacToeClient().startGui();
    }

    // Version graphique uniquement
    private void startGui() throws IOException {
        ClientSwingUI ui = new ClientSwingUI();
        AccueilUI accueil = AccueilUI.showDialog(ui);
        if (!accueil.isValidated()) System.exit(0);
        pseudo = accueil.getPseudo();
        char symboleChoisi = accueil.getSymbole();
        ui.setPseudo(pseudo);
        ui.setVisible(true);
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            out.println("PSEUDO:" + pseudo + ":" + symboleChoisi);

            Thread listener = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        handleServerMessageGui(line, ui, out);
                    }
                } catch (IOException e) {
                    ui.showMessage("Connexion perdue.");
                    System.exit(0);
                }
            });
            listener.setDaemon(true);
            listener.start();

            ui.setMoveListener((r, c) -> {
                if (myMark == '?' || currentTurn != myMark) return;
                out.println(Message.move(r, c));
            });

            while (!gameEnded) {
                try { Thread.sleep(200); } catch (InterruptedException e) {}
            }
        }
    }

    private void handleServerMessageGui(String line, ClientSwingUI ui, PrintWriter out) {
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
            String[] p = line.split(":");
            board = Board.deserialize(p[1]);
            currentTurn = p[2].charAt(0);
            ui.updateBoard(board, currentTurn);
        }
        else if (line.startsWith("END:")) {
            String res = line.substring(4);
            gameEnded = true;
            String msg;
            if (res.startsWith("WIN:")) {
                char winner = res.charAt(4);
                String winnerPseudo = (winner == myMark) ? pseudo : adversaire;
                String loserPseudo = (winner == myMark) ? adversaire : pseudo;
                msg = "Victoire de : " + winnerPseudo + "\nDéfaite de : " + loserPseudo;
            } else if (res.equals("DRAW")) {
                msg = "Match nul entre " + pseudo + (adversaire.isEmpty() ? "" : (" et " + adversaire));
            } else {
                msg = "Partie terminée → " + res;
            }
            ui.updateBoard(board, '-');
            ui.showMessage(msg);
            System.exit(0);
        }
        else if (line.startsWith("ERROR:")) {
            ui.showMessage("Erreur : " + line.substring(6));
        }
    }
}
