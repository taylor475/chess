package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData createUser(UserData userData) throws BadRequestException, DataAccessException {
        try {
            userDAO.createUser(userData);
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(authToken, userData.username());
        authDAO.addAuth(authData);

        return authData;
    }

    public AuthData loginUser(UserData userData) throws UnauthorizedException, DataAccessException {
        boolean userAuthenticated;
        try {
            userAuthenticated = userDAO.authenticateUser(userData.username(), userData.password());
        } catch (NotFoundException e) {
            throw new UnauthorizedException("User authentication failed: " + userData.username());
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }

        if (userAuthenticated) {
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(authToken, userData.username());
            authDAO.addAuth(authData);
            return authData;
        } else {
            throw new UnauthorizedException("User authentication failed: " + userData.username());
        }
    }

    public void logoutUser(String authToken) throws UnauthorizedException, DataAccessException {
        try {
            authDAO.getAuth(authToken);
        } catch (NotFoundException e) {
            throw new UnauthorizedException("No auth found: " + authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
        authDAO.deleteAuth(authToken);
    }

    public AuthData getAuth(String authToken) throws UnauthorizedException, DataAccessException {
        try {
            return authDAO.getAuth(authToken);
        } catch (NotFoundException e) {
            throw new UnauthorizedException("No auth found: " + authToken);
        } catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void clear() throws DataAccessException {
        userDAO.clear();
        authDAO.clear();
    }
}
