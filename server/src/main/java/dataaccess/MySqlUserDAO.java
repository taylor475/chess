package dataaccess;

import model.UserData;

import java.sql.*;
import java.util.HashSet;

import static java.sql.Types.NULL;

public class MySqlUserDAO implements UserDAO {
    public MySqlUserDAO() {
        db = HashSet.newHashSet(8);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        for (UserData user : db) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        throw new DataAccessException("User does not exist: " + username);
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {
        try {
            getUser(user.username());
        }
        // Failure to find the user means the user doesn't exist and can be added
        catch (DataAccessException e) {
            db.add(user);
            return;
        }
        throw new DataAccessException("User already exists: " + user.username());
    }

    @Override
    public boolean authenticateUser(String username, String password) throws DataAccessException {
        boolean userExists = false;
        for (UserData user : db) {
            if (user.username().equals(username)) {
                userExists = true;
            }
            if (user.username().equals(username) &&
                    user.password().equals(password)) {
                return true;
            }
        }
        // If the user exists but didn't return earlier, then the password is wrong
        if (userExists) {
            return false;
        } else {
            throw new DataAccessException("User does not exist: " + username);
        }
    }

    @Override
    public void clear() {
        db = HashSet.newHashSet(8);
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
            CREATE TABLE IF NOT EXISTS user (
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
}
