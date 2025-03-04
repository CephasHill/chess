package dataaccess;

import model.UserData;

import java.util.HashMap;

import static server.Server.database;

public class UserDataDAO {

    public void createUser(UserData u) throws DataAccessException {
        database.userMap.put(u.authToken(),u.username());
    }
    public void getUser(UserData u) throws DataAccessException {
        database.userMap.get(u.authToken());
    }
    public void clear() throws DataAccessException {
        database.userMap.clear();
    }
}
