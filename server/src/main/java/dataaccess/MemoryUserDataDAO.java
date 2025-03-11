package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.UUID;

import static server.Server.database;

public class MemoryUserDataDAO {

    public AuthData createUser(UserData u) throws DataAccessException {
        Pair<String, String> pair = new Pair<>(u.password(), u.email());
        database.userMap.put(u.username(), pair);
        String auth = generateAuth();
        database.authMap.put(auth, u.username());
        return new AuthData(u.username(),auth);
    }
    public AuthData loginUser(String username, String password) throws DataAccessException {
        try {
            getUser(username);
        }
        catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        if (database.userMap.get(username).getLeft().equals(password)) {
            String auth = generateAuth();
            database.authMap.put(auth, username);
            return new AuthData(username,auth);
        }
        else {
            throw new DataAccessException("Error: Wrong password");
        }
    }

    private String generateAuth() {
        return UUID.randomUUID().toString();
    }

    public void getUser(String username) throws DataAccessException {
        if (!database.userMap.containsKey(username)) {
            throw new DataAccessException("Error: User not found");
        }
    }

    public void logout(String authToken) throws DataAccessException {
        if (!database.authMap.containsKey(authToken)) {
            throw new DataAccessException("Error: AuthToken not found");
        }
        database.authMap.remove(authToken);
    }
}
