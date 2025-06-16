package client;

import common.Board;
import common.Message;
import common.Move;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;


/** Client console : envoie les coups et affiche le plateau. */
public class TicTacToeClient {
    private static final String HOST = "localhost";
    private static final int PORT = 5001;

    private volatile Board board = new Board();
    private volatile char myMark = '?';
    private volatile char currentTurn = 'X';
    private volatile boolean gameEnded = false;

    public static void main(String[] args) throws IOException {
        new TicTacToeClient().start();
    }

    private void start() throws IOException {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             Scanner scanner = new Scanner(System.in)) {

            // Thread d’écoute serveur
            Thread listener = new Thread(() -> {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        handleServerMessage(line);
                    }
                } catch (IOException e) {
                    System.out.println("[CLIENT] Connexion perdue.");
                    System.exit(0);
                }
            });
            listener.setDaemon(true);
            listener.start();

            // Boucle d’entrée utilisateur
            while (!gameEnded) {
                if (myMark == '?' || currentTurn != myMark) {
                    try { Thread.sleep(200); } catch (InterruptedException e) {}
                    continue;
                }
                System.out.print("(" + myMark + ") Entrer ligne colonne (0-2 0-2) : ");
                String input = scanner.nextLine();
                String[] parts = input.trim().split("\\s+");
                if (parts.length != 2) {
                    System.out.println("[CLIENT] Merci d'entrer deux nombres séparés par un espace.");
                    continue;
                }
                try {
                    int r = Integer.parseInt(parts[0]);
                    int c = Integer.parseInt(parts[1]);
                    out.println(Message.move(r, c));
                } catch (NumberFormatException e) {
                    System.out.println("[CLIENT] Merci d'entrer des nombres valides.");
                }
            }
        }
    }

    /* Parse les messages */
    private void handleServerMessage(String line) {
        if (line.startsWith("START:")) {
            myMark = line.charAt(6);
            System.out.println("[CLIENT] Vous jouez '" + myMark + "'.");
        }
        else if (line.startsWith("UPDATE:")) {
            String[] p = line.split(":");
            board = Board.deserialize(p[1]);
            currentTurn = p[2].charAt(0);
            ClientUI.render(board, currentTurn);
        }
        else if (line.startsWith("END:")) {
            String res = line.substring(4);
            gameEnded = true;
            if (res.startsWith("WIN:")) {
                char winner = res.charAt(4);
                if (winner == myMark) {
                    System.out.println("[CLIENT] Bravo, vous avez gagné !");
                } else {
                    System.out.println("[CLIENT] Vous avez perdu.");
                }
            } else if (res.equals("DRAW")) {
                System.out.println("[CLIENT] Match nul !");
            } else {
                System.out.println("[CLIENT] Partie terminée → " + res);
            }
            ClientUI.render(board, '-');
            System.exit(0);
        }
        else if (line.startsWith("ERROR:")) {
            System.out.println("[CLIENT] Erreur : " + line.substring(6));
        }
    }
}
