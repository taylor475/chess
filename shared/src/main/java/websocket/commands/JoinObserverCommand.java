package websocket.commands;

public class JoinObserverCommand extends UserGameCommand {
    public JoinObserverCommand(String authToken, int gameId) {
        super(CommandType.JOIN_OBSERVER, authToken, gameId);
    }
}
