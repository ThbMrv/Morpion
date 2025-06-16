package client;

import common.Board;

/** Vue console minimale. */
public class ClientUI {

    public static void render(Board b, char turn) {
        String s = b.serialize();
        System.out.println("\n╭─ Plateau ─╮");
        for (int i = 0; i < 9; i++) {
            char c = s.charAt(i);
            System.out.print((c == '-' ? '.' : c) + " ");
            if (i % 3 == 2) System.out.println();
        }
        if (turn == '-') {
            System.out.println("└─ Fin de partie ─");
        } else {
            System.out.println("Tour de : " + turn);
        }
    }
}
