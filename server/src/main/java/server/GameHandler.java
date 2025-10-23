package server;

import dataaccess.BadRequestException;
import dataaccess.UnauthorizedException;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import model.GameData;
import service.GameService;

import java.util.HashSet;
import java.util.Map;

public class GameHandler {
    private final GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    public void listGames(Context ctx) throws UnauthorizedException {
        String authToken = ctx.header("authorization");

        record GameSummary(Integer gameID, String gameName, String whiteUsername, String blackUsername) {}
        HashSet<GameSummary> games = gameService.listGames(authToken)
                .stream()
                .map(g -> new GameSummary(g.gameID(), g.gameName(), g.whiteUsername(), g.blackUsername()))
                .collect(java.util.stream.Collectors.toCollection(HashSet::new));

        record ListGamesResponse(HashSet<GameSummary> games) {}
        ctx.status(HttpStatus.OK).json(new ListGamesResponse(games));
    }

    public void createGame(Context ctx) throws BadRequestException, UnauthorizedException {
        if (!ctx.body().contains("\"gameName\":")) {
            throw new BadRequestException("Missing gameName");
        }

        GameData gameData = ctx.bodyAsClass(GameData.class);

        String authToken = ctx.header("authorization");
        int gameID = gameService.createGame(authToken, gameData.gameName());

        record CreateGameResponse(Integer gameID) {}
        ctx.status(HttpStatus.OK).json(new CreateGameResponse(gameID));
    }

    public void joinGame(Context ctx) throws BadRequestException, UnauthorizedException {
        if (!ctx.body().contains("\"gameID\":")) {
            throw new BadRequestException("Missing gameID");
        }

        String authToken = ctx.header("authorization");

        record JoinGameData(String playerColor, int gameID) {}
        JoinGameData joinData = ctx.bodyAsClass(JoinGameData.class);

        boolean joinSuccess = gameService.joinGame(authToken, joinData.gameID, joinData.playerColor);
        if (!joinSuccess) {
            ctx.status(HttpStatus.FORBIDDEN)
                    .json(Map.of("message", "Error: slot already taken"));
        }
        ctx.status(HttpStatus.OK).json(Map.of());
    }
}
