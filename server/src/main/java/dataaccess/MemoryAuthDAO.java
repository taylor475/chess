package dataaccess;

import model.AuthData;

import java.util.HashSet;

public class MemoryAuthDAO implements AuthDAO {
    private HashSet<AuthData> db;

    public MemoryAuthDAO() {
        db = HashSet.newHashSet(8);
    }

    @Override
    public void addAuth(AuthData authData) {
        db.add(authData);
    }

    @Override
    public void deleteAuth(String authToken) {
        for (AuthData authData : db) {
            if (authData.authToken().equals(authToken)) {
                db.remove(authData);
                break;
            }
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws NotFoundException {
        for (AuthData authData : db) {
            if (authData.authToken().equals(authToken)) {
                return authData;
            }
        }
        throw new NotFoundException("Auth token does not exist: " + authToken);
    }

    @Override
    public void clear() {
        db = HashSet.newHashSet(8);
    }
}
