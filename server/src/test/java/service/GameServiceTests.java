package service;

import dataaccess.*;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {
    private GameDAO gameDAO;
    private AuthDAO authDAO;
    private UserDAO userDAO;

    private GameService gameService;
    private UserService userService;

    private String tokenA;
    private String tokenB;

    @BeforeEach
    void setup() throws Exception {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();

        gameService = new GameService(gameDAO, authDAO);
        userService = new UserService(userDAO, authDAO);

        tokenA = userService.createUser(new UserData("taylor", "12345", "a@a.a")).authToken();
        tokenB = userService.createUser(new UserData("rolyat", "54321", "b@b.b")).authToken();
    }

    // listGames tests

    @Test
    void listGames_success() throws Exception {
        int id1 = gameService.createGame(tokenA, "g1");
        int id2 = gameService.createGame(tokenA, "g2");
        HashSet<GameData> games = gameService.listGames(tokenA);
        assertNotNull(games);
        assertTrue(games.stream().anyMatch(g -> g.gameID() == id1));
        assertTrue(games.stream().anyMatch(g -> g.gameID() == id2));
    }

    @Test
    void listGames_unauthorized() {
        assertThrows(UnauthorizedException.class, () -> gameService.listGames("bad-token"));
    }
}
