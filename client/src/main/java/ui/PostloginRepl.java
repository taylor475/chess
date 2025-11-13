package ui;

import chess.ChessGame;
import client.ServerFacade;
import model.GameData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.out;
import static ui.EscapeSequences.RESET_BG_COLOR;
import static ui.EscapeSequences.RESET_TEXT_COLOR;

public class PostloginRepl {
    ServerFacade server;
    List<GameData> games;
    boolean inGame;

    public PostloginRepl(ServerFacade server) {
        this.server = server;
        games = new ArrayList<>();
    }

    public void run() {
        boolean loggedIn = true;
        inGame = false;
        int gameNum;
        out.print(RESET_TEXT_COLOR + RESET_BG_COLOR);
        while (loggedIn && !inGame) {
            String[] input = getUserInput();
            switch (input[0]) {
                case "quit":
                    return;
                case "help":
                    printHelpMenu();
                    break;
                case "logout":
                    server.logout();
                    loggedIn = false;
                    break;
                case "list":
                    refreshGames();
                    printGames();
                    break;
                case "create":
                    if (input.length != 2) {
                        out.println("Please provide a name.");
                        printCreateInstr();
                        break;
                    }
                    server.createGame(input[1]);
                    out.printf("Created game: %s%n", input[1]);
                    break;
                case "join":
                    if (input.length != 3 || input[1].matches("\\d") || !input[2].toUpperCase().matches("WHITE|BLACK")) {
                        out.println("Please provide a game ID and color choice.");
                        printJoinInstr();
                        return;
                    }
                    gameNum = Integer.parseInt(input[1]);
                    if (games.isEmpty() || games.size() <= gameNum) {
                        refreshGames();
                        if (games.isEmpty()) {
                            out.println("Error: create a game first");
                            return;
                        }
                        if (games.size() <= gameNum) {
                            out.println("Error: Game ID does not exist");
                            printGames();
                            return;
                        }
                    }
                    GameData joinGame = games.get(gameNum);
                    ChessGame.TeamColor color = input[2].equalsIgnoreCase("WHITE") ?
                            ChessGame.TeamColor.WHITE
                            : ChessGame.TeamColor.BLACK;
                    if (server.joinGame(joinGame.gameID(), input[2].toUpperCase())) {
                        out.println("You have joined the game.");
                        inGame = true;
                        GameplayRepl gameplayRepl = new GameplayRepl(server, joinGame, color);
                        gameplayRepl.run();
                    } else {
                        out.println("Game does not exist or color is already taken.");
                        printJoinInstr();
                    }
                    break;
                case "observe":
                    if (input.length != 2 || !input[1].matches("\\d")) {
                        out.println("Please provide a game ID");
                        printObserveInstr();
                        return;
                    }
                    gameNum = Integer.parseInt(input[1]);
                    if (games.isEmpty() || games.size() <= gameNum) {
                        refreshGames();
                        if (games.isEmpty()) {
                            out.println("Error: create a game first");
                            return;
                        }
                        if (games.size() <= gameNum) {
                            out.println("Error: Game ID does not exist");
                            printGames();
                            return;
                        }
                    }
                    GameData observeGame = games.get(gameNum);
                    if (server.joinGame(observeGame.gameID(), null)) {
                        out.println("You have joined the game as an observer.");
                        inGame = true;
                        GameplayRepl gameplayRepl = new GameplayRepl(server, observeGame, null);
                        gameplayRepl.run();
                        return;
                    } else {
                        out.println("Game does not exist.");
                        printObserveInstr();
                        return;
                    }
                    break;
                default:
                    out.println("Command not recognized, please try again.");
                    printHelpMenu();
                    break;
            }
        }
        if (!loggedIn) {
            PreloginRepl preloginRepl = new PreloginRepl(server);
            preloginRepl.run();
        }
    }

    private String[] getUserInput() {
        out.print("\n[LOGGED IN] >>> ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().split(" ");
    }

    private void refreshGames() {
        games = new ArrayList<>();
        HashSet<GameData> gameList = server.listGames();
        games.addAll(gameList);
    }

    private void printGames() {
        for (int i = 0; i < games.size(); i++) {
            GameData game = games.get(i);
            String whiteUser = game.whiteUsername() != null ? game.whiteUsername() : "open";
            String blackUser = game.blackUsername() != null ? game.blackUsername() : "open";
            out.printf("%d -- Game Name: %s | White User: %s | Black User: %s %n", i, game.gameName(), whiteUser, blackUser);
        }
    }

    private void printHelpMenu() {
        printCreateInstr();
        out.println("list - list all games");
        printJoinInstr();
        printObserveInstr();
        out.println("logout - log out of current user");
        out.println("quit - stop playing");
        out.println("help - show this menu");
    }

    private void printCreateInstr() {
        out.println("create <NAME> - create a new game");
    }

    private void printJoinInstr() {
        out.println("join <ID> [WHITE|BLACK] - join a game as color");
    }

    private void printObserveInstr() {
        out.println("observe <ID> - observe a game");
    }
}
