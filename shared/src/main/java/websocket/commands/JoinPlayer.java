package websocket.commands;

import chess.ChessGame;

public class JoinPlayer extends UserGameCommand {
    ChessGame.TeamColor playerColor;

    public JoinPlayer(String authToken, int gameId, ChessGame.TeamColor playerColor) {
        super(CommandType.JOIN_PLAYER, authToken, gameId);
        this.playerColor = playerColor;
    }

    public ChessGame.TeamColor getPlayerColor() {
        return playerColor;
    }
}
