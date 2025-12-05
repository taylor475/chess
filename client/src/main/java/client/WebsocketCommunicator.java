package client;

import chess.ChessGame;
import com.google.gson.Gson;
import jakarta.websocket.*;
import ui.GameplayRepl;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static java.lang.System.out;
import static ui.EscapeSequences.ERASE_LINE;

public class WebsocketCommunicator extends Endpoint {
    Session session;

    public WebsocketCommunicator(String serverDomain) throws Exception {
        try {
            URI uri = new URI("ws://" + serverDomain + "/ws");

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, uri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    handleMessage(message);
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException exception) {
            throw new Exception();
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    private void handleMessage(String message) {
        ServerMessage base = new Gson().fromJson(message, ServerMessage.class);
        switch (base.getServerMessageType()) {
            case NOTIFICATION -> {
                NotificationMessage notification = new Gson().fromJson(message, NotificationMessage.class);
                printNotification(notification.getMessage());
            }
            case ERROR -> {
                ErrorMessage error = new Gson().fromJson(message, ErrorMessage.class);
                printNotification(error.getErrorMessage());
            }
            case LOAD_GAME -> {
                LoadGameMessage loadGame = new Gson().fromJson(message, LoadGameMessage.class);
                printLoadedGame(loadGame.getGame());
            }
        }
    }

    private void printNotification(String message) {
        out.print(ERASE_LINE + '\r');
        out.printf("\n%s\n[IN-GAME] >>> ", message);
    }

    private void printLoadedGame(ChessGame game) {
        out.print(ERASE_LINE + "\r\n");
        GameplayRepl.boardUi.updateGame(game);
        GameplayRepl.boardUi.printBoard(GameplayRepl.color, null);
        out.print("[IN-GAME] >>> ");
    }

    public void sendMessage(String message) {
        this.session.getAsyncRemote().sendText(message);
    }
}
