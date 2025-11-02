package dataaccess;

import dataaccess.exception.DataAccessException;
import dataaccess.exception.NotFoundException;
import model.AuthData;

import java.sql.*;

import static dataaccess.QueryManager.configureDatabase;
import static dataaccess.QueryManager.executeUpdate;

public class MySqlAuthDAO implements AuthDAO {
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS auth (
                username VARCHAR(255) NOT NULL,
                token VARCHAR(36) NOT NULL,
                PRIMARY KEY (token)
            )
            """
    };

    public MySqlAuthDAO() throws DataAccessException {
        configureDatabase(createStatements);
    }

    @Override
    public void addAuth(AuthData authData) throws DataAccessException {
        try {
            String statement = "INSERT INTO auth (username, token) VALUES (?, ?)";
            executeUpdate(statement, authData.username(), authData.authToken());
        } catch (DataAccessException e) {
            throw new DataAccessException(String.format("Error adding auth: %s", e));
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        try {
            String statement = "DELETE FROM auth WHERE token=?";
            executeUpdate(statement, authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException(String.format("Error deleting auth: %s", e));
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException, NotFoundException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, token FROM auth WHERE token=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String username = rs.getString("username");
                        String token = rs.getString("token");
                        return new AuthData(token, username);
                    }
                }
            }
            throw new NotFoundException(String.format("Auth token does not exist: %s", authToken));
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error getting auth: %s", e));
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            String statement = "TRUNCATE auth";
            executeUpdate(statement);
        } catch (DataAccessException e) {
            throw new DataAccessException(String.format("Error clearing auth: %s", e));
        }
    }
}
