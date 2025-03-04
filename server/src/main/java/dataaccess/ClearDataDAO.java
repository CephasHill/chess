package dataaccess;

import static server.Server.database;

public class ClearDataDAO {
    public void clearData() {
        if (!database.authMap.isEmpty()) {
            database.authMap.clear();
        }
        if (!database.userMap.isEmpty()) {
            database.userMap.clear();
        }
    }
}
