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
        } catch (NotFoundException e) {
            throw new UnauthorizedException("No auth found: " + authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException("Data access error: " + e.getMessage());
        }
        return gameDAO.listGames();
    }

    public GameData getGameData(String authToken, int gameID) throws UnauthorizedException, BadRequestException, DataAccessException {
        try {
            authDAO.getAuth(authToken);
        } catch (NotFoundException e) {
            throw new UnauthorizedException("No auth found: " + authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }

        try {
            return gameDAO.getGame(gameID);
        } catch (NotFoundException e) {
            throw new BadRequestException(e.getMessage());
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void updateGame(String authToken, GameData gameData) throws UnauthorizedException, BadRequestException, DataAccessException {
        try {
            authDAO.getAuth(authToken);
        } catch (NotFoundException e) {
            throw new UnauthorizedException("No auth found: " + authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }

        if (gameData == null) {
            throw new BadRequestException("No game data");
        }

        try {
            gameDAO.getGame(gameData.gameID());
        } catch (NotFoundException e) {
            throw new BadRequestException("Game not found: " + gameData.gameID());
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }

        try {
            gameDAO.updateGame(gameData);
        } catch (NotFoundException e) {
            throw new BadRequestException(e.getMessage());
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public int createGame(String authToken, String gameName) throws UnauthorizedException, BadRequestException, DataAccessException {
        try {
            authDAO.getAuth(authToken);
        } catch (NotFoundException e) {
            throw new UnauthorizedException("No auth found: " + authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
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
            throw new DataAccessException(e.getMessage());
        }

        return gameID;
    }

    public void joinGame(String authToken, int gameID, String color) throws UnauthorizedException, BadRequestException, ForbiddenException, DataAccessException {
        AuthData authData;
        GameData gameData;

        try {
            authData = authDAO.getAuth(authToken);
        } catch (NotFoundException e) {
            throw new UnauthorizedException("No auth found: " + authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }

        try {
            gameData = gameDAO.getGame(gameID);
        } catch (NotFoundException e) {
            throw new BadRequestException(e.getMessage());
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
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
        } catch (NotFoundException e) {
            throw new BadRequestException(e.getMessage());
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void clear() throws DataAccessException {
        gameDAO.clear();
    }
}
