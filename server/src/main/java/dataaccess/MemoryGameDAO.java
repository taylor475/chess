package dataaccess;

import model.GameData;

import java.util.HashSet;

public class MemoryGameDAO implements GameDAO {
    private HashSet<GameData> db;

    public MemoryGameDAO() {
        db = HashSet.newHashSet(8);
    }

    @Override
    public HashSet<GameData> listGames() {
        return db;
    }

    @Override
    public void createGame(GameData game) throws DataAccessException {
        db.add(game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData game : db) {
            if (game.gameID() == gameID) {
                return game;
            }
        }
        throw new DataAccessException("Game does not exist: " + gameID);
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        try {
            db.remove(getGame(game.gameID()));
            db.add(game);
        }
        catch (DataAccessException e) {
            db.add(game);
        }
    }

    @Override
    public void clear() {
        db = HashSet.newHashSet(8);
    }
}
