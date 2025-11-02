package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlUserDAOTests {
    UserDAO dao;

    @BeforeEach
    void setup() throws Exception {
        dao = new MySqlUserDAO();
        dao.clear();
    }

    @Test
    void createUserSuccess() throws Exception {
        UserData user = new UserData("taylor", "pw", "a@a.com");
        dao.createUser(user);

        UserData found = dao.getUser("taylor");
        assertEquals("taylor", found.username());
        assertNotEquals("pw", found.password());
        assertEquals("a@a.com", found.email());
    }

    @Test
    void createUserDuplicateFails() throws Exception {
        UserData user = new UserData("taylor", "pw", "a@a.com");
        dao.createUser(user);
        assertThrows(BadRequestException.class, () -> dao.createUser(user));
    }

    @Test
    void getUserSuccess() throws Exception {
        UserData user = new UserData("taylor", "pw", "a@a.com");
        UserData user2 = new UserData("rolyat", "pw2", "z@z.net");
        dao.createUser(user);
        dao.createUser(user2);

        UserData found = dao.getUser("taylor");
        assertEquals("taylor", found.username());
        assertNotEquals("pw", found.password());
        assertEquals("a@a.com", found.email());
    }

    @Test
    void getUserNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> dao.getUser("nope"));
    }

    @Test
    void authenticateUserSuccess() throws Exception {
        UserData user = new UserData("taylor", "pw", "a@a.com");
        dao.createUser(user);

        assertTrue(dao.authenticateUser("taylor", "pw"));
    }

    @Test
    void authenticateUserWrongPassword() throws Exception {
        UserData user = new UserData("taylor", "pw", "a@a.com");
        dao.createUser(user);

        assertFalse(dao.authenticateUser("taylor", "wrong"));
    }

    @Test
    void authenticateUserMissingUser() throws Exception {
        assertThrows(NotFoundException.class, () -> dao.authenticateUser("taylor", "pw"));
    }

    @Test
    void clearSuccess() throws Exception {
        UserData user = new UserData("taylor", "pw", "a@a.com");
        dao.createUser(user);
        dao.clear();
        assertThrows(NotFoundException.class, () -> dao.getUser("taylor"));
    }
}
