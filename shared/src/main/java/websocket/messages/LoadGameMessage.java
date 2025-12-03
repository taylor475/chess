package websocket.messages;

public class LoadGameMessage extends ServerMessage {
    String message;

    public LoadGameMessage(String message) {
        super(ServerMessageType.LOAD_GAME);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
