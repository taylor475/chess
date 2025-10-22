package server;

import com.google.gson.Gson;
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
}
