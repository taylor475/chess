package dataaccess;

import dataaccess.exception.DataAccessException;
import dataaccess.exception.NotFoundException;
import model.GameData;

import java.util.HashSet;

public interface GameDAO {
    HashSet<GameData> listGames() throws DataAccessException;

    void createGame(GameData game) throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException, NotFoundException;

    boolean gameExists(int gameID);

    void updateGame(GameData game) throws DataAccessException, NotFoundException;

    void clear() throws DataAccessException;
}
