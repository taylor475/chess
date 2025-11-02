package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.HashSet;

import static dataaccess.QueryManager.configureDatabase;
import static dataaccess.QueryManager.executeUpdate;

public class MySqlGameDAO implements GameDAO {
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS game (
                gameId INT NOT NULL,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255) NOT NULL,
                chessGame TEXT NOT NULL,
                PRIMARY KEY (gameId)
            )
            """
    };

    public MySqlGameDAO() throws DataAccessException {
        configureDatabase(createStatements);
    }

    @Override
    public HashSet<GameData> listGames() throws DataAccessException {
        HashSet<GameData> result = new HashSet<>(8);
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameId, whiteUsername, blackUsername, gameName, chessGame FROM game";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int gameId = rs.getInt("gameId");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        var text = rs.getString("chessGame");
                        ChessGame game = new Gson().fromJson(text, ChessGame.class);
                        result.add(new GameData(gameId, whiteUsername, blackUsername, gameName, game));
                    }
                }
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error listing games: %s", e.getMessage()));
        }
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        try {
            String statement = "INSERT INTO game (gameId, whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ? ,?)";
            String gameJson = new Gson().toJson(game);
            executeUpdate(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), gameJson);
        } catch (DataAccessException e) {
            throw new DataAccessException(String.format("Error creating game: %s", e.getMessage()));
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException, NotFoundException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT gameId, whiteUsername, blackUsername, gameName, chessGame FROM game WHERE gameId=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int gameId = rs.getInt("gameId");
                        String whiteUsername = rs.getString("whiteUsername");
                        String blackUsername = rs.getString("blackUsername");
                        String gameName = rs.getString("gameName");
                        var text = rs.getString("chessGame");
                        ChessGame game = new Gson().fromJson(text, ChessGame.class);
                        return new GameData(gameId, whiteUsername, blackUsername, gameName, game);
                    }
                }
            }
            throw new NotFoundException(String.format("Game does not exist: %s", gameID));
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error getting game: %s", e));
        }
    }

    @Override
    public boolean gameExists(int gameID) {
        try {
            getGame(gameID);
        } catch (NotFoundException | DataAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public void updateGame(GameData game) throws NotFoundException, DataAccessException {
        String statement = "UPDATE game SET whiteUsername=?, blackUsername=?, gameName=?, chessGame=? WHERE gameId=?";
        String gameJson = new Gson().toJson(game);
        int updated = executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), gameJson, game.gameID());
        if (updated == 0) {
            throw new NotFoundException(String.format("Game not found: %s", game.gameID()));
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            String statement = "TRUNCATE game";
            executeUpdate(statement);
        } catch (DataAccessException e) {
            throw new DataAccessException(String.format("Error clearing game: %s", e));
        }
    }
}
