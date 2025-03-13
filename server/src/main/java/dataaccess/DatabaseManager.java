package dataaccess;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }
    public String getConnectionUrl() {
        return CONNECTION_URL;
    }
    public String getConnectionUser() {
        return USER;
    }
    public String getConnectionPassword() {
        return PASSWORD;
    }
    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    public static void authorize(String authToken) throws DataAccessException {
        String query = "SELECT * FROM auth WHERE auth = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, authToken);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new DataAccessException("Error: Auth does not exist.");
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Database error: " + e.getMessage());
        }
    }


    /**
     * Creates the database if it does not already exist.
     */
    static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public static void initializeDatabase() throws SQLException {
        String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    username VARCHAR(256) UNIQUE NOT NULL,
                    password_hash VARCHAR(256) UNIQUE NOT NULL,
                    email VARCHAR(256) UNIQUE NOT NULL
                );""";
        String createAuthTable = """
                CREATE TABLE IF NOT EXISTS auth (
                    username VARCHAR(256) PRIMARY KEY,
                    auth VARCHAR(256) NOT NULL
                );""";
        String createGamesTable = """
                CREATE TABLE IF NOT EXISTS games (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    whiteUsername VARCHAR(256) NOT NULL,
                    blackUsername VARCHAR(256) NOT NULL,
                    gameName VARCHAR(256) NOT NULL,
                    chessGame JSON NOT NULL
                );""";
        try (Connection conn = getConnection();
            Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createUsersTable);
            stmt.executeUpdate(createAuthTable);
            stmt.executeUpdate(createGamesTable);
        }
    }

    public static void main(String[] args) {
        try {
            initializeDatabase();
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws SQLException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
