package server;

import com.google.gson.Gson;
import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import model.GameData;
import service.GameService;

import java.util.HashSet;

public class GameHandler {
    private GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public Object listGames(Context ctx) throws UnauthorizedException {
        String authToken = ctx.header("authorization");
        HashSet<GameData> games = gameService.listGames(authToken);

        ctx.status(HttpStatus.OK);
        return new Gson().toJson(games);
    }

    public Object createGame(Context ctx) throws BadRequestException, UnauthorizedException {
        if (!ctx.body().contains("\"gameName\":")) {
            throw new BadRequestException("Missing gameName");
        }

        GameData gameData = new Gson().fromJson(ctx.body(), GameData.class);

        String authToken = ctx.header("authorization");
        int gameID = gameService.createGame(authToken, gameData.gameName());

        ctx.status(HttpStatus.OK);
        return "{ \"gameID\": %d }".formatted(gameID);
    }

    public Object joinGame(Context ctx) throws BadRequestException, UnauthorizedException {
        if (!ctx.body().contains("\"gameID\":")) {
            throw new BadRequestException("Missing gameID");
        }

        String authToken = ctx.header("authorization");
        record JoinGameData(String playerColor, int gameID) {}
        JoinGameData joinData = new Gson().fromJson(ctx.body(), JoinGameData.class);
        boolean joinSuccess = gameService.joinGame(authToken, joinData.gameID, joinData.playerColor);

        if (!joinSuccess) {
            ctx.status(HttpStatus.FORBIDDEN);
            return "{ \"message\": \"Error: slot already taken\" }";
        }
        ctx.status(HttpStatus.OK);
        return "{}";
    }
}
