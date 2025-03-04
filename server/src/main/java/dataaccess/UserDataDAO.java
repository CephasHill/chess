package dataaccess;

import model.AuthData;
import model.UserData;

import static server.Server.database;

public class UserDataDAO {

    public void createUser(UserData u, AuthData a) throws DataAccessException {
        Pair<String, String> pair = new Pair<>(u.password(), u.email());
        database.userMap.put(u.username(), pair);
        database.authMap.put(a.authToken(),a.username());
    }
    public void getUser(UserData u) throws DataAccessException {
        if (!database.userMap.containsKey(u.username())) {
            throw new DataAccessException("Error: User not found");
        }
    }
}
