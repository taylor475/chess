package dataaccess;

import dataaccess.exception.BadRequestException;
import dataaccess.exception.DataAccessException;
import dataaccess.exception.NotFoundException;
import model.UserData;

import java.util.HashSet;

public class MemoryUserDAO implements UserDAO {
    private HashSet<UserData> db;

    public MemoryUserDAO() {
        db = HashSet.newHashSet(8);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException, NotFoundException {
        for (UserData user : db) {
            if (user.username().equals(username)) {
                return user;
            }
        }
        throw new NotFoundException("User does not exist: " + username);
    }

    @Override
    public void createUser(UserData user) throws BadRequestException {
        try {
            getUser(user.username());
        }
        // Failure to find the user means the user doesn't exist and can be added
        catch (DataAccessException | NotFoundException e) {
            db.add(user);
            return;
        }
        throw new BadRequestException("User already exists: " + user.username());
    }

    @Override
    public boolean authenticateUser(String username, String password) throws NotFoundException {
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
            throw new NotFoundException("User does not exist: " + username);
        }
    }

    @Override
    public void clear() {
        db = HashSet.newHashSet(8);
    }
}
