package dataaccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class AuthDataDAO {
    private HashMap<String, String> authMap;
    void createAuth(AuthData a) throws DataAccessException {
        authMap.put(a.authToken(),a.username());
    }
    void getAuth(AuthData a) throws DataAccessException {

    }
    void deleteAuth(AuthData a) throws DataAccessException {

    }
    void clear() throws DataAccessException {

    }
}
