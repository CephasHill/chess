package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {

    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;
    private static final DatabaseManager DATABASE_MANAGER = new DatabaseManager();


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
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port) + "/" + DATABASE_NAME;
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    @BeforeAll
    public static void setupDatabase() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DATABASE_MANAGER.getConnectionUrl(),
                DATABASE_MANAGER.getConnectionUser(),
                DATABASE_MANAGER.getConnectionPassword());
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DATABASE_MANAGER.getDatabaseName());
            stmt.executeUpdate("USE " + DATABASE_MANAGER.getDatabaseName());
            DatabaseManager.initializeDatabase(); // Creates tables
        }
    }

    @BeforeEach
    public void clearDatabase() throws SQLException {
        // Clear tables before each test
        try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM auth");
            stmt.executeUpdate("DELETE FROM users");
            stmt.executeUpdate("DELETE FROM games");
        }
    }

    @AfterAll
    public static void tearDownDatabase() throws SQLException {
        // Drop test database after all tests
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", USER, PASSWORD);
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
        try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next(), "User should exist in users table");
                assertEquals(username, rs.getString("username"));
                assertEquals(email, rs.getString("email"));
            }
        }

        // Verify auth in database
        try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
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
        try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
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
        try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
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
        try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD)) {
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
        try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
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
        try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
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