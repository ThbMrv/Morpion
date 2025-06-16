package server;

import common.Board;
import common.Move;
import common.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

/** Point d’entrée serveur, accepte 2 joueurs et orchestre la partie. */
public class TicTacToeServer {
    private static final int PORT = 5001;

    private final ServerSocket serverSocket;
    private final CopyOnWriteArrayList<PlayerHandler> players = new CopyOnWriteArrayList<>();
    private final Board board = new Board();
    private char currentTurn = 'X';

    public TicTacToeServer() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println("[SERVER] En écoute sur le port " + PORT);
    }

    public static void main(String[] args) throws IOException {
        new TicTacToeServer().start();
    }

    /** Boucle d’acceptation puis lancement de partie. */
    private void start() throws IOException {
        while (players.size() < 2) {
            Socket s = serverSocket.accept();
            char mark = players.isEmpty() ? 'X' : 'O';
            PlayerHandler ph = new PlayerHandler(s, this, mark);
            players.add(ph);
            ph.start();
            System.out.println("[SERVER] Joueur '" + mark + "' connecté.");
        }
        // début de partie
        for (PlayerHandler ph : players) {
            ph.send(Message.start(ph.getMark()));
        }
        broadcast(Message.update(board.serialize(), currentTurn));
    }

    /** Validation et application d’un MOVE reçu. */
    public synchronized void handleMove(PlayerHandler from, int r, int c) {
        if (from.getMark() != currentTurn) {
            from.send(Message.error("Ce n'est pas ton tour."));
            return;
        }
        if (!board.isMoveValid(r, c)) {
            from.send(Message.error("Coup invalide."));
            return;
        }
        board.applyMove(new Move(r, c, from.getMark()));
        currentTurn = (currentTurn == 'X') ? 'O' : 'X';

        char status = board.checkWinner();
        if (status == 'X' || status == 'O') {
            broadcast(Message.update(board.serialize(), '-'));
            broadcast(Message.end("WIN:" + status));
        } else if (status == 'D') {
            broadcast(Message.update(board.serialize(), '-'));
            broadcast(Message.end("DRAW"));
        } else {
            broadcast(Message.update(board.serialize(), currentTurn));
        }
    }

    public void broadcast(String msg) {
        for (PlayerHandler p : players) p.send(msg);
    }
}
