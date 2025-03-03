package dataaccess;

import model.UserData;

import java.util.HashMap;

public class UserDataDAO {
    private final HashMap<String, String> userMap;

    public UserDataDAO(HashMap<String, String> userMap) {
        this.userMap = userMap;
    }

    void createUser(UserData u) throws DataAccessException {
        userMap.put(u.authToken(),u.username());
    }
    void getUser(UserData u) throws DataAccessException {
        userMap.get(u.authToken());
    }
    void clear() throws DataAccessException {
        userMap.clear();
    }
}
