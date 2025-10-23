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
}
