package websocket.commands;

import chess.ChessGame;

public class JoinPlayerCommand extends UserGameCommand {
    ChessGame.TeamColor playerColor;

    public JoinPlayerCommand(String authToken, int gameId, ChessGame.TeamColor playerColor) {
        super(CommandType.JOIN_PLAYER, authToken, gameId);
        this.playerColor = playerColor;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}
