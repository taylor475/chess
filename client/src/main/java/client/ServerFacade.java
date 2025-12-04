package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import model.GameData;
import websocket.commands.*;

import java.util.HashSet;

import static java.lang.System.out;

public class ServerFacade {
    HttpCommunicator httpComm;
    WebsocketCommunicator wsComm;
    String serverDomain;
    String authToken;

    public ServerFacade() throws Exception {
        this("localhost:8080");
    }

    public ServerFacade(String serverDomain) throws Exception {
        this.serverDomain = serverDomain;
        httpComm = new HttpCommunicator(this, serverDomain);
    }

    protected String getAuthToken() {
        return authToken;
    }

    protected void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public boolean register(String username, String password, String email) {
        return httpComm.register(username, password, email);
    }

    public boolean login(String username, String password) {
        return httpComm.login(username, password);
    }

    public boolean logout() {
        return httpComm.logout();
    }

    public int createGame(String gameName) {
        return httpComm.createGame(gameName);
    }

    public HashSet<GameData> listGames() {
        return httpComm.listGames();
    }

    public boolean joinGame(int gameId, String playerColor) {
        return httpComm.joinGame(gameId, playerColor);
    }

    public void connectWebsocket() {
        try {
            wsComm = new WebsocketCommunicator(serverDomain);
        } catch (Exception e) {
            out.println("Failed to connect websocket with server.");
        }
    }

    public void sendCommand(UserGameCommand command) {
        String message = new Gson().toJson(command);
        wsComm.sendMessage(message);
    }

    public void joinPlayer(int gameId, ChessGame.TeamColor color) {
        sendCommand(new JoinPlayerCommand(authToken, gameId, color));
    }

    public void joinObserver(int gameId) {
        sendCommand(new JoinObserverCommand(authToken, gameId));
    }

    public void makeMove(int gameId, ChessMove move) {
        sendCommand(new MakeMoveCommand(authToken, gameId, move));
    }

    public void leave(int gameId) {
        sendCommand(new LeaveCommand(authToken, gameId));
    }

    public void resign(int gameId) {
        sendCommand(new ResignCommand(authToken, gameId));
    }
}
