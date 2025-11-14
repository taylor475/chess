package client;

import model.GameData;
import org.junit.jupiter.api.*;
import server.Server;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearDb() throws IOException {
        var url = new URL("http://localhost:" + port + "/db");
        var conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
        conn.connect();
        assertEquals(200, conn.getResponseCode(), "Failed to clear DB before test");
        conn.disconnect();
    }

    // ---------- Helpers ----------

    private client.ServerFacade newFacade() {
        try {
            return new client.ServerFacade("localhost:" + port);
        } catch (Exception e) {
            fail("Could not create ServerFacade: " + e.getMessage());
            return null;
        }
    }

    private void registerAndLogin(client.ServerFacade f, String u, String p, String email) {
        assertTrue(f.register(u, p, email), "register failed in helper");
        assertTrue(f.login(u, p), "login failed in helper");
    }

    // ---------- register ----------

    @Test
    public void registerNewUser() {
        var f = newFacade();
        assertTrue(f.register("taylor", "pass", "taylor@example.com"));
    }

    @Test
    public void registerDuplicateUsername() {
        var f = newFacade();
        assertTrue(f.register("taylor", "pass", "taylor@example.com"));
        assertFalse(f.register("taylor", "other", "taylor2@example.com"), "Duplicate username should fail");
    }

    // ---------- login ----------

    @Test
    public void loginCorrectCredentials() {
        var f = newFacade();
        assertTrue(f.register("taylor", "p@ss", "taylor@example.com"));
        assertTrue(f.login("taylor", "p@ss"));
    }

    @Test
    public void loginWrongPassword() {
        var f = newFacade();
        assertTrue(f.register("taylor", "secret", "taylor@example.com"));
        assertFalse(f.login("taylor", "wrong"), "Login should fail with wrong password");
    }

    // ---------- logout ----------

    @Test
    public void logoutWhenLoggedIn() {
        var f = newFacade();
        registerAndLogin(f, "taylor", "pw", "taylor@example.com");
        assertTrue(f.logout());
    }

    @Test
    public void logoutWhenNotLoggedIn() {
        var f = newFacade();
        assertFalse(f.logout());
    }

    // ---------- createGame ----------

    @Test
    public void createGameAfterLogin() {
        var f = newFacade();
        registerAndLogin(f, "taylor", "pw", "taylor@example.com");
        int gameId = f.createGame("TaylorGame");
        assertTrue(gameId > 0, "Expected a positive game ID");
    }

    @Test
    public void createGameWithoutLogin() {
        var f = newFacade();
        int gameId = f.createGame("NoAuthGame");
        assertEquals(-1, gameId, "createGame should fail without auth");
    }

    // ---------- listGames ----------

    @Test
    public void listGamesAfterCreating() {
        var f = newFacade();
        registerAndLogin(f, "taylor", "pw", "taylor@example.com");
        int id1 = f.createGame("taylor-1");
        int id2 = f.createGame("taylor-2");
        assertTrue(id1 > 0 && id2 > 0);

        HashSet<GameData> games = f.listGames();
        assertNotNull(games);
        assertTrue(games.size() >= 2, "Expected at least two games listed");
        boolean hasG1 = games.stream().anyMatch(g -> "taylor-1".equals(g.gameName()));
        boolean hasG2 = games.stream().anyMatch(g -> "taylor-2".equals(g.gameName()));
        assertTrue(hasG1 && hasG2, "List should include created game names");
    }

    @Test
    public void listGamesWithoutLogin() {
        var f = newFacade();
        HashSet<GameData> games = f.listGames();
        assertNotNull(games);
        assertEquals(0, games.size(), "listGames should be empty without auth");
    }

    // ---------- joinGame ----------

    @Test
    public void joinGameOpenColor() {
        var f = newFacade();
        registerAndLogin(f, "taylor", "pw", "taylor@example.com");
        int gameId = f.createGame("TaylorGame");
        assertTrue(gameId > 0);

        assertTrue(f.joinGame(gameId, "WHITE"), "Expected to join as WHITE");
    }

    @Test
    public void joinGameColorTakenByAnotherUser() {
        var f1 = newFacade();
        registerAndLogin(f1, "taylor", "pw", "taylor@example.com");
        int gameId = f1.createGame("TaylorGame");
        assertTrue(gameId > 0);
        assertTrue(f1.joinGame(gameId, "WHITE"));

        var f2 = newFacade();
        registerAndLogin(f2, "rolyat", "pw", "rolyat@example.com");
        assertFalse(f2.joinGame(gameId, "WHITE"), "Second user should not be able to take taken color");
    }
}
