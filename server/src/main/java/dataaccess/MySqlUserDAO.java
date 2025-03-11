package dataaccess;

import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.UUID;

import static server.Server.database;

public class MySqlUserDAO {
    public MySqlUserDAO() throws DataAccessException {
        configureDatabase();
    }

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
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  pet (
              `id` int NOT NULL AUTO_INCREMENT,
              `name` varchar(256) NOT NULL,
              `type` ENUM('CAT', 'DOG', 'FISH', 'FROG', 'ROCK') DEFAULT 'CAT',
              `json` TEXT DEFAULT NULL,
              PRIMARY KEY (`id`),
              INDEX(type),
              INDEX(name)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
