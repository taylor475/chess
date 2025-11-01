package dataaccess;

import com.google.gson.Gson;
import model.AuthData;

import java.sql.*;
import java.util.HashSet;

import static java.sql.Types.NULL;

public class MySqlAuthDAO implements AuthDAO {
    public MySqlAuthDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public void addAuth(AuthData authData) {
        try {
            String statement = "INSERT INTO auth (username, token) VALUES (?, ?)";
            executeUpdate(statement, authData.username(), authData.authToken());
        } catch (DataAccessException _) {
        }
    }

    @Override
    public void deleteAuth(String authToken) {
        try {
            String statement = "DELETE FROM auth WHERE token=?";
            executeUpdate(statement, authToken);
        } catch (DataAccessException _) {
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
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
            throw new DataAccessException(String.format("Auth token does not exist: %s", authToken));
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error getting auth: %s", e));
        }
    }

    @Override
    public void clear() {
        try {
            String statement = "TRUNCATE auth";
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
            CREATE TABLE IF NOT EXISTS auth (
                username VARCHAR(255) NOT NULL,
                token VARCHAR(36) NOT NULL,
                PRIMARY KEY (token)
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
