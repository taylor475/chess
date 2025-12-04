package websocket.commands;

public class ResignCommand extends UserGameCommand {
    public ResignCommand(String authToken, int gameId) {
        super(CommandType.RESIGN, authToken, gameId);
    }
}
