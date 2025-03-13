package dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static server.Server.database;

public class MySqlClearDAO {

    public static void clearData() throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS auth");
            stmt.executeUpdate("DROP TABLE IF EXISTS users");
            stmt.executeUpdate("DROP TABLE IF EXISTS games");
            // Optional: Recreate tables
             DatabaseManager.initializeDatabase();
        } catch (SQLException e) {
            throw new DataAccessException("Failed to drop tables: " + e.getMessage());
        }
    }
}

