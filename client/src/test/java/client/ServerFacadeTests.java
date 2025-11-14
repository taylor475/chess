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
    public void register_positive_newUser() {
        var f = newFacade();
        assertTrue(f.register("taylor", "pass", "taylor@example.com"));
    }

    @Test
    public void register_negative_duplicateUsername() {
        var f = newFacade();
        assertTrue(f.register("taylor", "pass", "taylor@example.com"));
        assertFalse(f.register("taylor", "other", "taylor2@example.com"), "Duplicate username should fail");
    }

    // ---------- login ----------

    @Test
    public void login_positive_correctCredentials() {
        var f = newFacade();
        assertTrue(f.register("taylor", "p@ss", "taylor@example.com"));
        assertTrue(f.login("taylor", "p@ss"));
    }

    @Test
    public void login_negative_wrongPassword() {
        var f = newFacade();
        assertTrue(f.register("taylor", "secret", "taylor@example.com"));
        assertFalse(f.login("taylor", "wrong"), "Login should fail with wrong password");
    }

    // ---------- logout ----------

    @Test
    public void logout_positive_whenLoggedIn() {
        var f = newFacade();
        registerAndLogin(f, "taylor", "pw", "taylor@example.com");
        assertTrue(f.logout());
    }

    @Test
    public void logout_negative_whenNotLoggedIn() {
        var f = newFacade();
        assertFalse(f.logout());
    }

    // ---------- createGame ----------

    @Test
    public void createGame_positive_afterLogin() {
        var f = newFacade();
        registerAndLogin(f, "taylor", "pw", "taylor@example.com");
        int gameId = f.createGame("TaylorGame");
        assertTrue(gameId > 0, "Expected a positive game ID");
    }

    @Test
    public void createGame_negative_withoutLogin() {
        var f = newFacade();
        int gameId = f.createGame("NoAuthGame");
        assertEquals(-1, gameId, "createGame should fail without auth");
    }

    // ---------- listGames ----------

    @Test
    public void listGames_positive_afterCreating() {
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
    public void listGames_negative_withoutLogin() {
        var f = newFacade();
        HashSet<GameData> games = f.listGames();
        assertNotNull(games);
        assertEquals(0, games.size(), "listGames should be empty without auth");
    }

    // ---------- joinGame ----------

    @Test
    public void joinGame_positive_openColor() {
        var f = newFacade();
        registerAndLogin(f, "taylor", "pw", "taylor@example.com");
        int gameId = f.createGame("TaylorGame");
        assertTrue(gameId > 0);

        assertTrue(f.joinGame(gameId, "WHITE"), "Expected to join as WHITE");
    }

    @Test
    public void joinGame_negative_colorTakenByAnotherUser() {
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
