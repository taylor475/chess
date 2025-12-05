package server;

import chess.ChessGame;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.exception.BadRequestException;
import dataaccess.exception.DataAccessException;
import dataaccess.exception.UnauthorizedException;
import io.javalin.websocket.WsContext;
import model.AuthData;
import model.GameData;
import websocket.commands.*;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.out;

public class WebsocketHandler {
    public static final Map<WsContext, Integer> gameSessions = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();

    public void onConnect(WsContext ctx) {
        gameSessions.put(ctx, 0);
    }

    public void onClose(WsContext ctx, int statusCode, String reason) {
        gameSessions.remove(ctx);
    }

    public void onMessage(WsContext ctx, String message) throws Exception {
        out.printf("Received: %s\n", message);

        UserGameCommand base = gson.fromJson(message, UserGameCommand.class);
        switch (base.getCommandType()) {
            case CONNECT -> {
                ConnectCommand command = gson.fromJson(message, ConnectCommand.class);
                gameSessions.put(ctx, command.getGameID());
                handleConnect(ctx, command);
            }
            case JOIN_PLAYER -> {
                JoinPlayerCommand command = gson.fromJson(message, JoinPlayerCommand.class);
                gameSessions.put(ctx, command.getGameID());
                handleJoinPlayer(ctx, command);
            }
            case JOIN_OBSERVER -> {
                JoinObserverCommand command = gson.fromJson(message, JoinObserverCommand.class);
                gameSessions.put(ctx, command.getGameID());
                handleJoinObserver(ctx, command);
            }
            case MAKE_MOVE -> {
                MakeMoveCommand command = gson.fromJson(message, MakeMoveCommand.class);
                handleMakeMove(ctx, command);
            }
            case LEAVE -> {
                LeaveCommand command = gson.fromJson(message, LeaveCommand.class);
                handleLeave(ctx, command);
            }
            case RESIGN -> {
                ResignCommand command = gson.fromJson(message, ResignCommand.class);
                handleResign(ctx, command);
            }
        }
    }

    private void handleConnect(WsContext ctx, ConnectCommand command) throws IOException {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            GameData game = Server.gameService.getGameData(command.getAuthToken(), command.getGameID());

            if (Objects.equals(game.whiteUsername(), auth.username())) {
                JoinPlayerCommand joinWhite = new JoinPlayerCommand(command.getAuthToken(), command.getGameID(), ChessGame.TeamColor.WHITE);
                handleJoinPlayer(ctx, joinWhite);
            } else if (Objects.equals(game.blackUsername(), auth.username())) {
                JoinPlayerCommand joinBlack = new JoinPlayerCommand(command.getAuthToken(), command.getGameID(), ChessGame.TeamColor.BLACK);
                handleJoinPlayer(ctx, joinBlack);
            } else {
                JoinObserverCommand joinObserver = new JoinObserverCommand(command.getAuthToken(), command.getGameID());
                handleJoinObserver(ctx, joinObserver);
            }
        } catch (UnauthorizedException e) {
            sendError(ctx, new Error("Error: Unauthorized"));
        } catch (BadRequestException e) {
            sendError(ctx, new Error("Error: Invalid game"));
        } catch (DataAccessException e) {
            sendError(ctx, new Error("Error: Database failure"));
        }
    }

    private void handleJoinPlayer(WsContext ctx, JoinPlayerCommand command) throws IOException {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            GameData game = Server.gameService.getGameData(command.getAuthToken(), command.getGameID());

            ChessGame.TeamColor joiningColor = command.getPlayerColor().toString().equalsIgnoreCase("white")
                    ? ChessGame.TeamColor.WHITE
                    : ChessGame.TeamColor.BLACK;

            boolean correctColor;
            if (joiningColor == ChessGame.TeamColor.WHITE) {
                correctColor = Objects.equals(game.whiteUsername(), auth.username());
            } else {
                correctColor = Objects.equals(game.blackUsername(), auth.username());
            }

            if (!correctColor) {
                Error error = new Error("Error: attempting to joing with wrong color");
                sendError(ctx, error);
                return;
            }

            NotificationMessage notif = new NotificationMessage("%s has joined the game as %s".formatted(auth.username(), command.getPlayerColor().toString()));
            broadcastMessage(ctx, notif);

            LoadGameMessage load = new LoadGameMessage(game.game());
            sendMessage(ctx, load);
        } catch (UnauthorizedException e) {
            sendError(ctx, new Error("Error: Unauthorized"));
        } catch (BadRequestException e) {
            sendError(ctx, new Error("Error: Invalid game"));
        } catch (DataAccessException e) {
            sendError(ctx, new Error("Error: Database failure"));
        }
    }

    private void handleJoinObserver(WsContext ctx, JoinObserverCommand command) throws IOException {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            GameData game = Server.gameService.getGameData(command.getAuthToken(), command.getGameID());

            NotificationMessage notif = new NotificationMessage("%s has joined the game as an observer".formatted(auth.username()));
            broadcastMessage(ctx, notif);

            LoadGameMessage load = new LoadGameMessage(game.game());
            sendMessage(ctx, load);
        } catch (UnauthorizedException e) {
            sendError(ctx, new Error("Error: Unauthorized"));
        } catch (BadRequestException e) {
            sendError(ctx, new Error("Error: Invalid game"));
        } catch (DataAccessException e) {
            sendError(ctx, new Error("Error: Database failure"));
        }
    }

    private void handleMakeMove(WsContext ctx, MakeMoveCommand command) throws IOException {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            GameData game = Server.gameService.getGameData(command.getAuthToken(), command.getGameID());

            ChessGame.TeamColor userColor = getTeamColor(auth.username(), game);
            if (userColor == null) {
                sendError(ctx, new Error("Error: Observers can't make moves"));
                return;
            }

            if (game.game().getGameOver()) {
                sendError(ctx, new Error("Error: Game has concluded"));
                return;
            }

            if (game.game().getTeamTurn().equals(userColor)) {
                game.game().makeMove(command.getMove());

                NotificationMessage notif;
                ChessGame.TeamColor opponentColor = userColor == ChessGame.TeamColor.WHITE
                        ? ChessGame.TeamColor.BLACK
                        : ChessGame.TeamColor.WHITE;

                if (game.game().isInCheckmate(opponentColor)) {
                    notif = new NotificationMessage("Checkmate! %s wins!".formatted(auth.username()));
                    game.game().setGameOver(true);
                } else if (game.game().isInStalemate(opponentColor)) {
                    notif = new NotificationMessage("Stalemate caused by %s. It's a tie!".formatted(auth.username()));
                    game.game().setGameOver(true);
                } else if (game.game().isInCheck(opponentColor)) {
                    notif = new NotificationMessage("A move has been made by %s, %s is now in check!".formatted(auth.username(), opponentColor.toString()));
                } else {
                    notif = new NotificationMessage("A move has been made by %s".formatted(auth.username()));
                }
                broadcastMessage(ctx, notif);

                Server.gameService.updateGame(auth.authToken(), game);

                LoadGameMessage load = new LoadGameMessage(game.game());
                broadcastMessage(ctx, load, true);
            } else {
                sendError(ctx, new Error("Error: it is not your turn"));
            }
        } catch (UnauthorizedException e) {
            sendError(ctx, new Error("Error: Unauthorized"));
        } catch (BadRequestException e) {
            sendError(ctx, new Error("Error: Invalid game"));
        } catch (DataAccessException e) {
            sendError(ctx, new Error("Error: Database failure"));
        } catch (InvalidMoveException e) {
            sendError(ctx, new Error("Error: Invalid move"));
        }
    }

    private void handleLeave(WsContext ctx, LeaveCommand command) throws IOException {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());

            NotificationMessage notif = new NotificationMessage("%s has left the game".formatted(auth.username()));
            broadcastMessage(ctx, notif);

            ctx.session.close();
        } catch (UnauthorizedException e) {
            sendError(ctx, new Error("Error: Unauthorized"));
        } catch (DataAccessException e) {
            sendError(ctx, new Error("Error: Database failure"));
        }
    }

    private void handleResign(WsContext ctx, ResignCommand command) throws IOException {
        try {
            AuthData auth = Server.userService.getAuth(command.getAuthToken());
            GameData game = Server.gameService.getGameData(command.getAuthToken(), command.getGameID());
            ChessGame.TeamColor userColor = getTeamColor(auth.username(), game);

            String opponentUsername = userColor == ChessGame.TeamColor.WHITE
                    ? game.blackUsername()
                    : game.whiteUsername();

            if (userColor == null) {
                sendError(ctx, new Error("Error: Observers can't resign"));
                return;
            }

            if (game.game().getGameOver()) {
                sendError(ctx, new Error("Error: The game has already concluded"));
                return;
            }

            game.game().setGameOver(true);
            Server.gameService.updateGame(auth.authToken(), game);

            NotificationMessage notif = new NotificationMessage("%s has forfeited, %s wins!".formatted(auth.username(), opponentUsername));
            broadcastMessage(ctx, notif, true);
        } catch (UnauthorizedException e) {
            sendError(ctx, new Error("Error: Unauthorized"));
        } catch (BadRequestException e) {
            sendError(ctx, new Error("Error: Invalid game"));
        } catch (DataAccessException e) {
            sendError(ctx, new Error("Error: Database failure"));
        }
    }

    // Broadcast notification to all clients on the game except the current session
    public void broadcastMessage(WsContext currCtx, ServerMessage message) throws IOException {
        broadcastMessage(currCtx, message, false);
    }

    // Broadcast notification to all clients on the game
    public void broadcastMessage(WsContext currCtx, ServerMessage message, boolean toSelf) throws IOException {
        out.printf("Broadcasting (toSelf: %s): %s%n", toSelf, new Gson().toJson(message));
        for (WsContext ctx : gameSessions.keySet()) {
            boolean inGame = gameSessions.get(ctx) != 0;
            boolean sameGame = gameSessions.get(ctx).equals(gameSessions.get(currCtx));
            boolean isSelf = ctx == currCtx;
            if ((toSelf || !isSelf) && inGame && sameGame) {
                sendMessage(ctx, message);
            }
        }
    }

    public void sendMessage(WsContext ctx, ServerMessage message) throws IOException {
        ctx.send(gson.toJson(message));
    }

    public void sendError(WsContext ctx, Error error) throws IOException {
        out.printf("Error: %s%n", new Gson().toJson(error));
        ctx.send(gson.toJson(error));
    }

    private ChessGame.TeamColor getTeamColor(String username, GameData game) {
        if (username.equals(game.whiteUsername())) {
            return ChessGame.TeamColor.WHITE;
        } else if (username.equals(game.blackUsername())) {
            return ChessGame.TeamColor.BLACK;
        } else {
            return null;
        }
    }
}
