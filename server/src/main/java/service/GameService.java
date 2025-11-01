package service;

import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;

import java.util.HashSet;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public HashSet<GameData> listGames(String authToken) throws UnauthorizedException, DataAccessException {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("No auth found: " + authToken);
        }
        return gameDAO.listGames();
    }

    public GameData getGameData(String authToken, int gameID) throws UnauthorizedException, BadRequestException {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("No auth found: " + authToken);
        }

        try {
            return gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public void updateGame(String authToken, GameData gameData) throws UnauthorizedException, BadRequestException {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("No auth found: " + authToken);
        }

        if (gameData == null) {
            throw new BadRequestException("No game data");
        }

        try {
            gameDAO.getGame(gameData.gameID());
        } catch (DataAccessException e) {
            throw new BadRequestException("Game not found: " + gameData.gameID());
        }

        try {
            gameDAO.updateGame(gameData);
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public int createGame(String authToken, String gameName) throws UnauthorizedException, BadRequestException {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("No auth found: " + authToken);
        }

        Random random = ThreadLocalRandom.current();
        int gameID = -1;
        for (int attempts = 0; attempts < 10000; attempts++) {
            int candidate = random.nextInt(1, 10000);
            if (!gameDAO.gameExists(candidate)) {
                gameID = candidate;
                break;
            }
        }

        if (gameID == -1) {
            throw new BadRequestException("No game IDs left.");
        }

        try {
            ChessGame game = new ChessGame();
            ChessBoard board = new ChessBoard();
            board.resetBoard();
            game.setBoard(board);
            gameDAO.createGame(new GameData(gameID, null, null, gameName, game));
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }

        return gameID;
    }

    public void joinGame(String authToken, int gameID, String color) throws UnauthorizedException, BadRequestException, ForbiddenException {
        AuthData authData;
        GameData gameData;

        try {
            authData = authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("No auth found: " + authToken);
        }

        try {
            gameData = gameDAO.getGame(gameID);
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }

        String whiteUser = gameData.whiteUsername();
        String blackUser = gameData.blackUsername();

        if (Objects.equals(color, "WHITE")) {
            if (whiteUser != null && !whiteUser.equals(authData.username())) {
                throw new ForbiddenException("Color already taken"); // white is already taken by someone else
            } else {
                whiteUser = authData.username();
            }
        } else if (Objects.equals(color, "BLACK")) {
            if (blackUser != null && !blackUser.equals(authData.username())) {
                throw new ForbiddenException("Color already taken"); // black is already taken by someone else
            } else {
                blackUser = authData.username();
            }
        } else if (color != null) {
            throw new BadRequestException("Invalid team color: " + color);
        }

        try {
            gameDAO.updateGame(new GameData(gameID, whiteUser, blackUser, gameData.gameName(), gameData.game()));
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }
}
