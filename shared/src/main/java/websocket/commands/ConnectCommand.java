package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    public ConnectCommand(String authToken, int gameId) {
        super(CommandType.CONNECT, authToken, gameId);
    }
}
