package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
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
        boardUi.printBoard(color, null);
        while (inGame) {
            String[] input = getUserInput();
            switch (input[0]) {
                case "help":
                    printHelpMenu();
                    break;
                case "redraw":
                    boardUi.printBoard(color, null);
                    break;
                case "leave":
                    inGame = false;
                    server.leave(gameId);
                    break;
                case "move":
                    handleMove(input);
                    break;
                case "resign":
                    out.println("Are you sure you want to resign? (y/n)");
                    String[] confirmation = getUserInput();
                    if (confirmation.length == 1 && confirmation[0].equalsIgnoreCase("y")) {
                        server.resign(gameId);
                    } else {
                        out.println("Resignation cancelled.");
                    }
                    break;
                case "highlight":
                    if (input.length == 2 && input[1].matches("[a-h][1-8]")) {
                        ChessPosition position = new ChessPosition(input[1].charAt(1) - '0', input[1].charAt(0) - ('a' - 1));
                        boardUi.printBoard(color, position);
                    } else {
                        out.println("Please provide a valid coordinate (e.g. 'c4')");
                        printHighlightInstr();
                    }
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
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) {
            return new String[]{""};
        }
        return line.split("\\s+");
    }

    private void printHelpMenu() {
        out.println("redraw - redraw the game board");
        out.println("leave - leave the current game");
        printMoveInstr();
        out.println("resign - forfeit this game");
        printHighlightInstr();
        out.println("help - show this menu");
    }

    private void printMoveInstr() {
        out.println("move <from> <to> <promotion piece> - make a move (only include promotion piece when a move will promote a pawn)");
    }

    private void printHighlightInstr() {
        out.println("highlight <coordinate> - highlight all legal moves for the selected piece");
    }

    private void handleMove(String[] input) {
        if (input.length >= 3 && input[1].matches("[a-h][1-8]") && input[2].matches("[a-h][1-8]")) {
            ChessPosition from = new ChessPosition(input[1].charAt(1) - '0', input[1].charAt(0) - ('a' - 1));
            ChessPosition to = new ChessPosition(input[2].charAt(1) - '0', input[2].charAt(0) - ('a' - 1));

            ChessPiece.PieceType promotion = null;
            if (input.length == 4) {
                promotion = getPieceType(input[3]);
                // Handles cases where the user inputs an invalid piece
                if (promotion == null) {
                    out.println("Provide a valid promotion piece option (e.g. 'queen')");
                    printMoveInstr();
                }
            }

            server.makeMove(gameId, new ChessMove(from, to, promotion));
        } else {
            out.println("Please provide a valid to and from coordinate (e.g. 'c4 h8')");
            printMoveInstr();
        }
    }

    public ChessPiece.PieceType getPieceType(String name) {
        // PAWN is excluded because pawns are required to promote when they reach the end of the board
        return switch (name.toUpperCase()) {
            case "QUEEN" -> ChessPiece.PieceType.QUEEN;
            case "KNIGHT" -> ChessPiece.PieceType.KNIGHT;
            case "BISHOP" -> ChessPiece.PieceType.BISHOP;
            case "ROOK" -> ChessPiece.PieceType.ROOK;
            default -> null;
        };
    }
}
