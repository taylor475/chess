package dataaccess;

import com.google.gson.Gson;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.HashSet;

import static java.sql.Types.NULL;

public class MySqlUserDAO implements UserDAO {
    public MySqlUserDAO() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public UserData getUser(String username) throws DataAccessException, NotFoundException {
        try (Connection conn = DatabaseManager.getConnection()) {
            String statement = "SELECT username, password, email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String user = rs.getString("username");
                        String password = rs.getString("password");
                        String email = rs.getString("email");
                        return new UserData(user, password, email);
                    }
                }
            }
            throw new NotFoundException(String.format("User does not exist: %s", username));
        } catch (SQLException e) {
            throw new DataAccessException(String.format("Error getting user: %s", e.getMessage()));
        }
    }

    @Override
    public void createUser(UserData user) throws DataAccessException, BadRequestException {
        try {
            getUser(user.username());
            throw new BadRequestException("User already exists: " + user.username());
        }
        // Failure to find the user means the user doesn't exist and can be added
        catch (NotFoundException | DataAccessException e) {
            String statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            executeUpdate(statement, user.username(), hashPassword(user.password()), user.email());
        }
    }

    @Override
    public boolean authenticateUser(String username, String password) throws DataAccessException, NotFoundException {
        UserData user = getUser(username);
        return BCrypt.checkpw(password, user.password());
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            String statement = "TRUNCATE users";
            executeUpdate(statement);
        } catch (DataAccessException e) {
            throw new DataAccessException(String.format("Error clearing users: %s", e));
        }
    }

    private void executeUpdate(String statement, Object... params) throws DataAccessException {
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
                    rs.getInt(1);
                }

            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException(String.format("Unable to update database: %s", e.getMessage()));
        }
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
                username VARCHAR(255) NOT NULL,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255),
                PRIMARY KEY (username)
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

    private String hashPassword(String password) {
        String salt = BCrypt.gensalt();
        return BCrypt.hashpw(password, salt);
    }
}
