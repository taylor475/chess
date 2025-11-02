package service;

import dataaccess.*;
import dataaccess.exception.BadRequestException;
import dataaccess.exception.NotFoundException;
import dataaccess.exception.UnauthorizedException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    private UserDAO userDAO;
    private AuthDAO authDAO;
    private UserService userService;

    @BeforeEach
    void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    // createUser tests

    @Test
    void createUserSuccess() throws Exception {
        UserData user = new UserData("taylor", "12345", "a@a.a");
        AuthData auth = userService.createUser(user);
        assertNotNull(auth);
        assertEquals("taylor", auth.username());
        assertEquals("taylor", authDAO.getAuth(auth.authToken()).username());
        assertTrue(userDAO.authenticateUser("taylor", "12345"));
    }

    @Test
    void createUserDuplicate() throws Exception {
        UserData user = new UserData("taylor", "12345", "a@a.a");
        userService.createUser(user);
        assertThrows(BadRequestException.class, () -> userService.createUser(user));
    }

    // loginUser tests

    @Test
    void loginUserSuccess() throws Exception {
        UserData user = new UserData("taylor", "12345", "a@a.a");
        userService.createUser(user);
        AuthData auth = userService.loginUser(new UserData("taylor", "12345", null));
        assertNotNull(auth);
        assertEquals("taylor", auth.username());
        assertEquals("taylor", authDAO.getAuth(auth.authToken()).username());
    }

    @Test
    void loginUserWrongPassword() throws Exception {
        UserData user = new UserData("taylor", "12345", "a@a.a");
        userService.createUser(user);
        assertThrows(UnauthorizedException.class,
                () -> userService.loginUser(new UserData("taylor", "no", null)));
    }

    // logoutUser tests

    @Test
    void logoutUserSuccess() throws Exception {
        UserData user = new UserData("taylor", "12345", "a@a.a");
        AuthData auth = userService.createUser(user);
        userService.logoutUser(auth.authToken());
        assertThrows(UnauthorizedException.class, () -> userService.getAuth(auth.authToken()));
    }

    @Test
    void logoutUserInvalidToken() {
        assertThrows(UnauthorizedException.class, () -> userService.logoutUser("no"));
    }

    // getAuth tests

    @Test
    void getAuthSuccess() throws Exception {
        UserData user = new UserData("taylor", "12345", "a@a.a");
        AuthData auth = userService.createUser(user);
        AuthData lookUp = userService.getAuth(auth.authToken());
        assertEquals(auth.username(), lookUp.username());
    }

    @Test
    void getAuthInvalid() {
        assertThrows(UnauthorizedException.class, () -> userService.getAuth("missing"));
    }

    // clear tests

    @Test
    void clearResetsUsersAndAuth() throws Exception {
        UserData user = new UserData("taylor", "12345", "a@a.a");
        AuthData auth = userService.createUser(user);
        userService.clear();

        assertThrows(UnauthorizedException.class, () -> userService.getAuth(auth.authToken()));
        assertThrows(NotFoundException.class, () -> userDAO.authenticateUser("taylor", "12345"));
    }
}
