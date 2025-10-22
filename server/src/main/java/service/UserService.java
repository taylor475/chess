package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData createUser(UserData userData) throws BadRequestException {
        try {
            userDAO.createUser(userData);
        } catch (DataAccessException e) {
            throw new BadRequestException(e.getMessage());
        }
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(userData.username(), authToken);
        authDAO.addAuth(authData);

        return authData;
    }

    public AuthData loginUser(UserData userData) throws UnauthorizedException {
        boolean userAuthenticated;
        try {
            userAuthenticated = userDAO.authenticateUser(userData.username(), userData.password());
        } catch (DataAccessException e) {
            throw new UnauthorizedException("User authentication failed: " + userData.username());
        }

        if (userAuthenticated) {
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(userData.username(), authToken);
            authDAO.addAuth(authData);
            return authData;
        } else {
            throw new UnauthorizedException("User authentication failed: " + userData.username());
        }
    }

    public void logoutUser(String authToken) throws UnauthorizedException {
        try {
            authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("No auth found: " + authToken);
        }
        authDAO.deleteAuth(authToken);
    }

    public AuthData getAuth(String authToken) throws UnauthorizedException {
        try {
            return authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            throw new UnauthorizedException("No auth found: " + authToken);
        }
    }

    public void clear() {
        userDAO.clear();
        authDAO.clear();
    }
}
