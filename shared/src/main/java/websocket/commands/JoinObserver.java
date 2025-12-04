package websocket.commands;

public class JoinObserver extends UserGameCommand {
    public JoinObserver(String authToken, int gameId) {
        super(CommandType.JOIN_OBSERVER, authToken, gameId);
    }
}
