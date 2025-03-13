package dataaccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static server.Server.database;

public class MySqlClearDAO {

    public void clearData() throws SQLException {
        DatabaseManager dbm = new DatabaseManager();
        try (Connection conn = DriverManager.getConnection(dbm.getConnectionUrl(), dbm.getConnectionUser(), dbm.getConnectionPassword());
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM auth");
            stmt.executeUpdate("DELETE FROM users");
        }
    }
}
