package dataaccess;

import java.sql.SQLException;

import static server.Server.database;

public class MySqlClearDAO {
    public MySqlClearDAO() throws DataAccessException {
        configureDatabase();
    }

    public void clearData() {
        if (!database.authMap.isEmpty()) {
            database.authMap.clear();
        }
        if (!database.userMap.isEmpty()) {
            database.userMap.clear();
        }
        if (!database.gameMap.isEmpty()) {
            database.gameMap.clear();
        }
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
