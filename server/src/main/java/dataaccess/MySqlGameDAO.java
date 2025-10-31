package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.sql.*;
import java.util.HashSet;

import static java.sql.Types.NULL;

public class MySqlGameDAO implements GameDAO {
    public MySqlGameDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public HashSet<GameData> listGames() {
        HashSet<GameData> result = new HashSet<GameData>(8);
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
        } catch (Exception _) {
        }
        return result;
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        try {
            String statement = "INSERT INTO game (gameId, whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ? ,?)";
            String jsonStatement = new Gson().toJson(game);
            executeUpdate(statement, game.gameID(), game.whiteUsername(), game.blackUsername(), game.gameName(), game.game(), jsonStatement);
        } catch (DataAccessException _) {
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
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
        } catch (DataAccessException | SQLException e) {
            throw new DataAccessException(String.format("Game does not exist: %s", gameID));
        }
        return null;
    }

    @Override
    public boolean gameExists(int gameID) {
        try {
            getGame(gameID);
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        try {
            String statement = "UPDATE game SET whiteUsername=?, blackUsername=?, gameName=?, chessGame=? WHERE gameId=?";
            String jsonStatement = new Gson().toJson(game);
            executeUpdate(statement, game.whiteUsername(), game.blackUsername(), game.gameName(), game.game(), game.gameID(), jsonStatement);
        } catch (DataAccessException _) {
        }
    }

    @Override
    public void clear() {
        try {
            String statement = "TRUNCATE game";
            executeUpdate(statement);
        } catch (DataAccessException _) {
        }
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) {
                        ps.setString(i + 1, p);
                    }
                    else if (param instanceof Integer p) {
                        ps.setInt(i + 1, p);
                    } else if (param == null) {
                        ps.setNull(i + 1, NULL);
                    }
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(String.format("Unable to update database: %s", e.getMessage()));
        }
    }

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

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(String.format("Unable to configure database: %s", e.getMessage()));
        }
    }
}
