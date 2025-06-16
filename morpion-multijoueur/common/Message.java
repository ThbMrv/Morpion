package common;

/** Fabrique les messages texte simples échangés via socket. */
public class Message {

    public static String move(int r, int c) {
        return "MOVE:" + r + "," + c;
    }

    public static String update(String board, char turn) {
        return "UPDATE:" + board + ":" + turn;
    }

    public static String start(char yourMark) {
        return "START:" + yourMark;
    }

    public static String end(String result) {
        return "END:" + result;        // WIN:X, WIN:O ou DRAW
    }

    public static String error(String msg) {
        return "ERROR:" + msg;
    }
}
