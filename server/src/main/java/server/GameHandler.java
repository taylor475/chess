package server;

import service.GameService;

public class GameHandler {
    private GameService gameService;

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }
}
