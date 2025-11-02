package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void addAuth(AuthData authData) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException, NotFoundException;

    void clear() throws DataAccessException;
}
