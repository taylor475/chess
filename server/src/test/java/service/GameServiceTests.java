package service;

import chess.ChessGame;
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

    // getGameData tests

    @Test
    void getGameData_success() throws Exception {
        int id = gameService.createGame(tokenA, "g1");
        GameData g = gameService.getGameData(tokenA, id);
        assertEquals(id, g.gameID());
        assertEquals("g1", g.gameName());
        assertNotNull(g.game());
    }

    @Test
    void getGameData_badId_BadRequest() {
        assertThrows(BadRequestException.class, () -> gameService.getGameData(tokenA, 9999));
    }

    // updateGame tests

    @Test
    void updateGame_success() throws Exception {
        int id = gameService.createGame(tokenA, "update");
        GameData before = gameDAO.getGame(id);
        assertNull(before.whiteUsername());
        assertNull(before.blackUsername());

        GameData after = new GameData(id, "taylor", "rolyat", before.gameName(), before.game());
        gameService.updateGame(tokenA, after);

        GameData stored = gameDAO.getGame(id);
        assertEquals("taylor", stored.whiteUsername());
        assertEquals("rolyat", stored.blackUsername());
    }

    @Test
    void updateGame_nonexistentId_BadRequest() {
        GameData fake = new GameData(1111, null, null, "none", new ChessGame());
        assertThrows(BadRequestException.class, () -> gameService.updateGame(tokenA, fake));
    }

    // createGame tests

    @Test
    void createGame_success() throws Exception {
        int id = gameService.createGame(tokenA, "new");
        assertTrue(id > 0);
        GameData g = gameDAO.getGame(id);
        assertEquals("new", g.gameName());
        assertNotNull(g.game());
    }

    @Test
    void createGame_unauthorized() {
        assertThrows(UnauthorizedException.class, () -> gameService.createGame("no", "no"));
    }

    // joinGame tests

    @Test
    void joinGame_success_white() throws Exception {
        int id = gameService.createGame(tokenA, "game1");
        gameService.joinGame(tokenA, id, "WHITE");
        GameData g = gameDAO.getGame(id);
        assertEquals("taylor", g.whiteUsername());
        assertNull(g.blackUsername());
    }
}
