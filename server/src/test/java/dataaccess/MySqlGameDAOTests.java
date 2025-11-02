package dataaccess;

import chess.ChessGame;
import dataaccess.exception.DataAccessException;
import dataaccess.exception.NotFoundException;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class MySqlGameDAOTests {
    GameDAO dao;

    @BeforeEach
    void setup() throws Exception {
        dao = new MySqlGameDAO();
        dao.clear();
    }

    @Test
    void createGameSuccess() throws Exception {
        GameData game = new GameData(101, null, null, "game101", new ChessGame());
        dao.createGame(game);

        GameData found = dao.getGame(101);
        assertEquals(101, found.gameID());
        assertEquals("game101", found.gameName());
        assertNull(found.whiteUsername());
        assertNull(found.blackUsername());
        assertNotNull(found.game());
    }

    @Test
    void createGameDuplicate() throws Exception {
        GameData game = new GameData(101, null, null, "game101", new ChessGame());
        GameData game2 = new GameData(101, null, null, "game102", new ChessGame());
        dao.createGame(game);
        assertThrows(DataAccessException.class, () -> dao.createGame(game2));
    }

    @Test
    void getGameSuccess() throws Exception {
        GameData game = new GameData(101, null, null, "game101", new ChessGame());
        GameData game2 = new GameData(102, null, null, "game102", new ChessGame());
        dao.createGame(game);
        dao.createGame(game2);

        GameData found = dao.getGame(101);
        assertEquals(101, found.gameID());
        assertEquals("game101", found.gameName());
        assertNull(found.whiteUsername());
        assertNull(found.blackUsername());
        assertNotNull(found.game());
    }

    @Test
    void getGameNotFound() throws Exception {
        assertThrows(NotFoundException.class, () -> dao.getGame(101));
    }

    @Test
    void listGamesSuccess() throws Exception {
        GameData game = new GameData(101, null, null, "game101", new ChessGame());
        GameData game2 = new GameData(102, null, null, "game102", new ChessGame());
        dao.createGame(game);
        dao.createGame(game2);

        HashSet<GameData> list = dao.listGames();
        assertTrue(list.stream().anyMatch(g -> g.gameID() == 101));
        assertTrue(list.stream().anyMatch(g -> g.gameID() == 102));
    }

    @Test
    void listGamesNoGames() throws Exception {
        assertTrue(dao.listGames().isEmpty());
    }

    @Test
    void updateGameSuccess() throws Exception {
        GameData game = new GameData(101, null, null, "game101", new ChessGame());
        dao.createGame(game);

        GameData update = new GameData(101, "taylor", "rolyat", "game101", new ChessGame());
        dao.updateGame(update);

        GameData found = dao.getGame(101);
        assertEquals("taylor", found.whiteUsername());
        assertEquals("rolyat", found.blackUsername());
        assertNotNull(found.game());
    }

    @Test
    void updateGameMissingGame() throws Exception {
        GameData update = new GameData(101, "taylor", "rolyat", "game101", new ChessGame());
        assertThrows(NotFoundException.class, () -> dao.updateGame(update));
    }

    @Test
    void clearSuccess() throws Exception {
        GameData game = new GameData(101, null, null, "game101", new ChessGame());
        GameData game2 = new GameData(102, null, null, "game102", new ChessGame());
        dao.createGame(game);
        dao.createGame(game2);

        dao.clear();

        HashSet<GameData> all = dao.listGames();
        assertTrue(all.isEmpty());
    }
}
