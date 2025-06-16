package client;

import common.Board;
import common.Message;

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

    public static void main(String[] args) throws IOException {
        new TicTacToeClient().startGui();
    }

    // Version graphique uniquement
    private void startGui() throws IOException {
        ClientSwingUI ui = new ClientSwingUI();
        ui.setVisible(true);
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Thread d’écoute serveur
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

            // Boucle d'attente (empêche la fermeture de la fenêtre)
            while (!gameEnded) {
                try { Thread.sleep(200); } catch (InterruptedException e) {}
            }
        }
    }

    // Gestion des messages pour l'UI graphique
    private void handleServerMessageGui(String line, ClientSwingUI ui, PrintWriter out) {
        if (line.startsWith("START:")) {
            myMark = line.charAt(6);
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
                if (winner == myMark) {
                    msg = "Bravo, vous avez gagné !";
                } else {
                    msg = "Vous avez perdu.";
                }
            } else if (res.equals("DRAW")) {
                msg = "Match nul !";
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
