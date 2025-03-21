package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {
    private static String dbUser = "";
    private static String dbPassword = "";
    private static String dbConnectionUrl = "";
    private static final DatabaseManager databaseManager = new DatabaseManager();

    public DataAccessTests() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new IOException("Could not find resource 'db.properties'.");
            }
            properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String dbName = properties.getProperty("db.name");
        dbUser = properties.getProperty("db.user");
        dbPassword = properties.getProperty("db.password");
        var host = properties.getProperty("db.host");
        var port = Integer.parseInt(properties.getProperty("db.port"));
        dbConnectionUrl = String.format("jdbc:mysql://%s:%d/%s", host, port, dbName);
    }

    @BeforeAll
    public static void setupDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(databaseManager.getConnectionUrl(),
                databaseManager.getConnectionUser(),
                databaseManager.getConnectionPassword());
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + databaseManager.getDatabaseName());
            stmt.executeUpdate("USE " + databaseManager.getDatabaseName());
            DatabaseManager.initializeDatabase(); // Creates tables
        }
    }

    @BeforeEach
    public void clearDatabase() throws SQLException {
        // Clear tables before each test
        try (Connection conn = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM auth");
            stmt.executeUpdate("DELETE FROM users");
            stmt.executeUpdate("DELETE FROM games");
        }
    }

    @AfterAll
    public static void tearDownDatabase() throws SQLException {
        // Drop test database after all tests
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", dbUser, dbPassword);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP DATABASE IF EXISTS chess_db_test");
        }
    }

    @Test
    public void testRegisterUserSuccess() throws Exception {
        // Test successful registration
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        MySqlUserDAO dao = new MySqlUserDAO();
        dao.createUser(new UserData(username, password, email));

        // Verify user in database
        try (Connection conn = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next(), "User should exist in users table");
                assertEquals(username, rs.getString("username"));
                assertEquals(email, rs.getString("email"));
            }
        }

        // Verify auth in database
        try (Connection conn = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM auth WHERE username = (SELECT username FROM users WHERE username = ?)")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next(), "Auth entry should exist");
            }
        }
    }

    @Test
    public void testRegisterUserDuplicateUsername() {
        // Test duplicate username failure
        String username = "duplicate";
        String email1 = "email1@example.com";
        String email2 = "email2@example.com";
        String password = "password123";

        try {
            MySqlUserDAO dao = new MySqlUserDAO();
            UserData u = new UserData(username, email1, password);
            dao.createUser(u); // First registration
            assertThrows(DataAccessException.class, () -> dao.createUser(u),
                    "Should throw SQLException for duplicate username");
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void testLogoutSuccess() throws Exception {
        // Test successful registration
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        MySqlUserDAO dao = new MySqlUserDAO();
        AuthData a = dao.createUser(new UserData(username, password, email));
        dao.logout(a.authToken());
        try (Connection conn = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM auth WHERE username = (SELECT username FROM users WHERE username = ?)")) {
            ps.setString(1, username);
            ps.executeQuery();
            try (ResultSet rs = ps.executeQuery()) {
                assertFalse(rs.next(), "Auth entry should not exist");
            }
        }
    }
    @Test
    public void testLogoutFail() throws Exception {
        // Test successful registration
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        MySqlUserDAO dao = new MySqlUserDAO();
        AuthData a = dao.createUser(new UserData(username, password, email));
        try {
            dao.logout("dummy");
            fail("Should throw SQLException");
        }
        catch (DataAccessException e) {
            assertNotNull(e);
        }
    }
    @Test
    public void testLoginSuccess() throws Exception {
        // Test successful registration
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        MySqlUserDAO dao = new MySqlUserDAO();
        AuthData a = dao.createUser(new UserData(username, password, email));
        dao.logout(a.authToken());
        dao.loginUser(username, password);
        try (Connection conn = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM auth WHERE username = (SELECT username FROM users WHERE username = ?)")) {
            ps.setString(1, username);
            ps.executeQuery();
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next(), "Auth entry should exist");
            }
        }
    }
    @Test
    public void testLoginFail() throws Exception {
        // Test successful registration
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        MySqlUserDAO dao = new MySqlUserDAO();
        AuthData a = dao.createUser(new UserData(username, password, email));
        dao.logout(a.authToken());
        try {
            dao.loginUser(username, "wrongPassword");
            fail("Should throw SQLException");
        }
        catch (DataAccessException e) {
            assertNotNull(e);
        }
    }
    @Test
    public void testClearSuccess() throws Exception {
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        MySqlClearDAO dao = new MySqlClearDAO();
        dao.clearData();
        try (Connection conn = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword)) {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM auth");
            ps.executeQuery();
            try (ResultSet rs = ps.executeQuery()) {
                assertFalse(rs.next(), "Data entry should not exist");
            }
            ps = conn.prepareStatement("SELECT * FROM users");
            ps.executeQuery();
            try (ResultSet rs = ps.executeQuery()) {
                assertFalse(rs.next(), "Data entry should not exist");
            }
            ps = conn.prepareStatement("SELECT * FROM games");
            ps.executeQuery();
            try (ResultSet rs = ps.executeQuery()) {
                assertFalse(rs.next(), "Auth entry should not exist");
            }
        }
    }
    @Test
    public void testClearFail() throws Exception {
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        MySqlClearDAO dao = new MySqlClearDAO();
        dao.clearData();
        assertFalse(false);
    }
    @Test
    public void testCreateGameSuccess() throws Exception {
        // Test successful registration
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        MySqlUserDAO userDao = new MySqlUserDAO();
        AuthData a = userDao.createUser(new UserData(username, password, email));
        MySqlGameDAO gameDao = new MySqlGameDAO();
        gameDao.createGame("game name", a.authToken());
        try (Connection conn = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM games WHERE gameName = ?")) {
            ps.setString(1, "game name");
            ps.executeQuery();
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next(), "Game entry should exist");
            }
        }
    }
    @Test
    public void testCreateGameFail() throws Exception {
        // Test successful registration
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        MySqlUserDAO userDao = new MySqlUserDAO();
        AuthData a = userDao.createUser(new UserData(username, password, email));
        MySqlGameDAO gameDao = new MySqlGameDAO();
        try {
            gameDao.createGame("game name", "bad authToken");
            fail("Should throw SQLException");
        }
        catch (DataAccessException e) {
            assertNotNull(e);
        }
    }
    @Test
    public void testListGamesSuccess() throws Exception {
        MySqlUserDAO userDao = new MySqlUserDAO();
        AuthData auth = userDao.createUser(new UserData("testuser", "password123", "test@example.com"));
        MySqlGameDAO gameDao = new MySqlGameDAO();

        gameDao.createGame("Game1", auth.authToken());
        gameDao.createGame("Game2", auth.authToken());

        ArrayList<GameData> games = gameDao.listGames(auth.authToken());
        assertEquals(2, games.size(), "Should return 2 games");
        assertTrue(games.stream().anyMatch(g -> "Game1".equals(g.gameName())), "Game1 should exist");
        assertTrue(games.stream().anyMatch(g -> "Game2".equals(g.gameName())), "Game2 should exist");
    }
    @Test
    public void testListGamesFail() throws Exception {
        MySqlUserDAO userDao = new MySqlUserDAO();
        AuthData auth = userDao.createUser(new UserData("testuser", "password123", "test@example.com"));
        MySqlGameDAO gameDao = new MySqlGameDAO();
        try {
            gameDao.createGame("Game2", "bad authToken");
            fail("Should throw SQLException");
        } catch (DataAccessException e) {
            assertNotNull(e);
        }
    }
    @Test
    public void testJoinGameSuccess() throws Exception {
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        MySqlUserDAO userDao = new MySqlUserDAO();
        AuthData a = userDao.createUser(new UserData(username, password, email));
        MySqlGameDAO gameDao = new MySqlGameDAO();
        GameData g = gameDao.createGame("game name", a.authToken());
        gameDao.join("white", g.gameID(), a.authToken());
        try (Connection conn = DriverManager.getConnection(dbConnectionUrl, dbUser, dbPassword);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM games WHERE gameName = ? && id = ?")) {
            ps.setString(1, "game name");
            ps.setInt(2, g.gameID());
            ps.executeQuery();
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next(), "Game entry should exist");
            }
        }
    }
    @Test
    public void testJoinGameFail() throws Exception {
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        MySqlUserDAO userDao = new MySqlUserDAO();
        AuthData a = userDao.createUser(new UserData(username, password, email));
        MySqlGameDAO gameDao = new MySqlGameDAO();
        try {
            GameData g = gameDao.createGame("game name", a.authToken());
            gameDao.join("green", g.gameID(), a.authToken());
            fail("Should throw SQLException");
        }
        catch (DataAccessException e) {
            assertNotNull(e);
        }
    }
}