package ui;

import chess.ChessGame;
import chess.ChessPiece;
import client.ServerFacade;
import model.GameData;

import java.util.Scanner;

import static java.lang.System.out;
import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class GameplayRepl {
    ServerFacade server;
    public static BoardUi boardUi;
    ChessGame game;
    int gameId;
    public static ChessGame.TeamColor color;

    public GameplayRepl(ServerFacade server, GameData gameData, ChessGame.TeamColor color) {
        this.server = server;
        this.game = gameData.game();
        this.gameId = gameData.gameID();
        GameplayRepl.color = color;
        boardUi = new BoardUi(game);
    }

    public void run() {
        boolean inGame = true;
        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        while (inGame) {
            String[] input = getUserInput();
            switch (input[0]) {
                case "help":
                    printHelpMenu();
                    break;
                case "leave":
                    inGame = false;
                    break;
                default:
                    out.println("Command not recognized, please try again.");
                    printHelpMenu();
                    break;
            }
        }
        PostloginRepl postloginRepl = new PostloginRepl(server);
        postloginRepl.run();
    }

    private String[] getUserInput() {
        out.print("\n[IN-GAME] >>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }

    private void printHelpMenu() {
        out.println("leave - leave the current game");
        out.println("help - show this menu");
    }
}
