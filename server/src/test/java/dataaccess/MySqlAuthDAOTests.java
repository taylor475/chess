package dataaccess;

import dataaccess.exception.DataAccessException;
import dataaccess.exception.NotFoundException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlAuthDAOTests {
    AuthDAO dao;

    @BeforeEach
    void setup() throws Exception {
        dao = new MySqlAuthDAO();
        dao.clear();
    }

    @Test
    void addAuthSuccess() throws Exception {
        AuthData auth = new AuthData("token", "taylor");
        dao.addAuth(auth);

        AuthData found = dao.getAuth("token");
        assertEquals("taylor", found.username());
        assertEquals("token", found.authToken());
    }

    @Test
    void addAuthDuplicateFails() throws Exception {
        AuthData auth1 = new AuthData("token", "taylor");
        AuthData auth2 = new AuthData("token", "rolyat");
        dao.addAuth(auth1);
        assertThrows(DataAccessException.class, () -> dao.addAuth(auth2));
    }

    @Test
    void getAuthSuccess() throws Exception {
        AuthData auth = new AuthData("token", "taylor");
        AuthData auth2 = new AuthData("token2", "rolyat");
        dao.addAuth(auth);
        dao.addAuth(auth2);

        AuthData found = dao.getAuth("token");
        assertEquals("taylor", found.username());
        assertEquals("token", found.authToken());
        assertNotEquals("rolyat", found.username());
        assertNotEquals("token2", found.authToken());
    }

    @Test
    void getAuthNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> dao.getAuth("none"));
    }

    @Test
    void deleteAuthSuccess() throws Exception {
        AuthData auth = new AuthData("token", "taylor");
        dao.addAuth(auth);
        dao.deleteAuth("token");
        assertThrows(NotFoundException.class, () -> dao.getAuth("token"));
    }

    @Test
    void deleteAuthMissing() throws Exception {
        assertDoesNotThrow(() -> dao.deleteAuth("token"));
    }

    @Test
    void clearSuccess() throws Exception {
        AuthData auth = new AuthData("token", "taylor");
        AuthData auth2 = new AuthData("token2", "rolyat");
        dao.addAuth(auth);
        dao.addAuth(auth2);

        dao.clear();

        assertThrows(NotFoundException.class, () -> dao.getAuth("token"));
        assertThrows(NotFoundException.class, () -> dao.getAuth("token2"));
    }
}
