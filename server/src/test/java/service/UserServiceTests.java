package service;

import dataaccess.*;
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
    void createUser_success() throws Exception {
        UserData user = new UserData("taylor", "12345", "a@a.a");
        AuthData auth = userService.createUser(user);
        assertNotNull(auth);
        assertEquals("taylor", auth.username());
        assertEquals("taylor", authDAO.getAuth(auth.authToken()).username());
        assertTrue(userDAO.authenticateUser("taylor", "12345"));
    }

    @Test
    void createUser_duplicate_badRequest() throws Exception {
        UserData user = new UserData("taylor", "12345", "a@a.a");
        userService.createUser(user);
        assertThrows(BadRequestException.class, () -> userService.createUser(user));
    }

    // loginUser tests

    @Test
    void loginUser_success() throws Exception {
        UserData user = new UserData("taylor", "12345", "a@a.a");
        userService.createUser(user);
        AuthData auth = userService.loginUser(new UserData("taylor", "12345", null));
        assertNotNull(auth);
        assertEquals("taylor", auth.username());
        assertEquals("taylor", authDAO.getAuth(auth.authToken()).username());
    }

    @Test
    void loginUser_wrongPassword_unauthorized() throws Exception {
        UserData user = new UserData("taylor", "12345", "a@a.a");
        userService.createUser(user);
        assertThrows(UnauthorizedException.class,
                () -> userService.loginUser(new UserData("taylor", "no", null)));
    }

    // logoutUser tests

    @Test
    void logoutUser_success() throws Exception {
        UserData user = new UserData("taylor", "12345", "a@a.a");
        AuthData auth = userService.createUser(user);
        userService.logoutUser(auth.authToken());
        assertThrows(UnauthorizedException.class, () -> userService.getAuth(auth.authToken()));
    }

    @Test
    void logoutUser_invalidToken_unauthorized() {
        assertThrows(UnauthorizedException.class, () -> userService.logoutUser("no"));
    }

    // getAuth tests

    @Test
    void getAuth_success() throws Exception {
        UserData user = new UserData("taylor", "12345", "a@a.a");
        AuthData auth = userService.createUser(user);
        AuthData lookUp = userService.getAuth(auth.authToken());
        assertEquals(auth.username(), lookUp.username());
    }
}
