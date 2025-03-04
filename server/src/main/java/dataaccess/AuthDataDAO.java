package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

import static server.Server.database;

public class AuthDataDAO {
    void createAuth(AuthData a) throws DataAccessException {
        database.authMap.put(a.authToken(),a.username());
    }
    AuthData getAuth(AuthData a) throws DataAccessException {
        if (database.authMap.containsKey(a.authToken())) {
            return a;
        }
        else {
            return null;
        }
    }
    void deleteAuth(AuthData a) throws DataAccessException {
        database.authMap.remove(a.authToken());
    }
    void clear() throws DataAccessException {
        database.authMap.clear();
    }
}
