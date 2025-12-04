package websocket.commands;

public class LeaveCommand extends UserGameCommand {
    public LeaveCommand(String authToken, int gameId) {
        super(CommandType.LEAVE, authToken, gameId);
    }
}
