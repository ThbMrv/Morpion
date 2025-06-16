package server;

import common.Move;
import common.Message;

import java.io.*;
import java.net.Socket;


/** Un thread par joueur – lit ses messages et les relaie au serveur. */
public class PlayerHandler extends Thread {
    private final Socket socket;
    private final TicTacToeServer server;
    private final BufferedReader in;
    private final PrintWriter out;
    private final char mark;
    private String pseudo = "";

    public PlayerHandler(Socket socket, TicTacToeServer server, char mark) throws IOException {
        this.socket = socket;
        this.server = server;
        this.mark = mark;
        in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public char getMark() { return mark; }
    public String getPseudo() { return pseudo; }
    public void send(String msg) { out.println(msg); }

    @Override
    public void run() {
        try {
            String line = in.readLine();
            char symbole = mark;
            if (line != null && line.startsWith("PSEUDO:")) {
                String[] parts = line.split(":");
                if (parts.length >= 3) {
                    pseudo = parts[1];
                    char demande = parts[2].charAt(0);
                    // Si c'est le premier joueur, il choisit, sinon on attribue l'autre symbole
                    if (server.isSymboleDispo(demande)) {
                        symbole = demande;
                    } else {
                        symbole = (server.isSymboleDispo('X')) ? 'X' : 'O';
                    }
                }
            }
            String adv = server.getAdversairePseudo(this);
            send(Message.start(String.valueOf(symbole) + (adv != null ? ":" + adv : "")));
            while ((line = in.readLine()) != null) {
                if (line.startsWith("MOVE:")) {
                    String[] parts = line.substring(5).split(",");
                    int r = Integer.parseInt(parts[0]);
                    int c = Integer.parseInt(parts[1]);
                    server.handleMove(this, r, c);
                }
            }
        } catch (IOException e) {
            System.err.println("[SERVER] Joueur '" + mark + "' déconnecté.");
        }
    }
}