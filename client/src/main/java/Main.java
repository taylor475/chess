import chess.*;
import client.ServerFacade;
import ui.PreloginRepl;

import static java.lang.System.out;

public class Main {
    public static void main(String[] args) throws Exception {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        out.println("♕ 240 Chess Client: " + piece);

        ServerFacade server = new ServerFacade();

        PreloginRepl prelogin = new PreloginRepl(server);
        prelogin.run();
        out.println("Exited program.");
    }
}