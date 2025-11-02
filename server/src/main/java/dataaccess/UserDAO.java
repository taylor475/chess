package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData getUser(String username) throws DataAccessException, NotFoundException;

    void createUser(UserData user) throws DataAccessException;

    boolean authenticateUser(String username, String password) throws DataAccessException, NotFoundException;

    void clear() throws DataAccessException;
}
