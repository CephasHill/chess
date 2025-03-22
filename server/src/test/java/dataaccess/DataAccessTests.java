package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {

    private static final DatabaseManager DATABASE_MANAGER = new DatabaseManager();

    @BeforeAll
    public static void setupDatabase() throws SQLException, DataAccessException {
        // Create and initialize the database using DatabaseManager
        DatabaseManager.createDatabase();
        try (Connection conn = DatabaseManager.getConnection()) {
            DatabaseManager.initializeDatabase(); // Creates tables
        }
    }

    @BeforeEach
    public void clearDatabase() throws SQLException {
        // Clear tables before each test using DatabaseManager's connection
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM auth");
            stmt.executeUpdate("DELETE FROM users");
            stmt.executeUpdate("DELETE FROM games");
        }
    }

    @AfterAll
    public static void tearDownDatabase() throws SQLException {
        // Drop the database after all tests
        try (Connection conn = DriverManager.getConnection(
                DATABASE_MANAGER.getConnectionUrl(),
                DATABASE_MANAGER.getConnectionUser(),
                DATABASE_MANAGER.getConnectionPassword());
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DROP DATABASE IF EXISTS " + DATABASE_MANAGER.getDatabaseName());
        }
    }

    @Test
    public void testRegisterUserSuccess() throws Exception {
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        MySqlUserDAO dao = new MySqlUserDAO();
        dao.createUser(new UserData(username, password, email));

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next(), "User should exist in users table");
                assertEquals(username, rs.getString("username"));
                assertEquals(email, rs.getString("email"));
            }
        }

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM auth WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next(), "Auth entry should exist");
            }
        }
    }

    @Test
    public void testRegisterUserDuplicateUsername() throws Exception {
        String username = "duplicate";
        String email1 = "email1@example.com";
        String email2 = "email2@example.com";
        String password = "password123";

        MySqlUserDAO dao = new MySqlUserDAO();
        UserData u = new UserData(username, password, email1);
        dao.createUser(u); // First registration
        assertThrows(DataAccessException.class, () -> dao.createUser(u),
                "Should throw DataAccessException for duplicate username");
    }

    @Test
    public void testLogoutSuccess() throws Exception {
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        MySqlUserDAO dao = new MySqlUserDAO();
        AuthData a = dao.createUser(new UserData(username, password, email));
        dao.logout(a.authToken());
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM auth WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                assertFalse(rs.next(), "Auth entry should not exist");
            }
        }
    }

    @Test
    public void testLogoutFail() throws Exception {
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        MySqlUserDAO dao = new MySqlUserDAO();
        AuthData a = dao.createUser(new UserData(username, password, email));
        assertThrows(DataAccessException.class, () -> dao.logout("dummy"),
                "Should throw DataAccessException for invalid auth token");
    }

    @Test
    public void testLoginSuccess() throws Exception {
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        MySqlUserDAO dao = new MySqlUserDAO();
        AuthData a = dao.createUser(new UserData(username, password, email));
        dao.logout(a.authToken());
        dao.loginUser(username, password);
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM auth WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next(), "Auth entry should exist");
            }
        }
    }

    @Test
    public void testLoginFail() throws Exception {
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        MySqlUserDAO dao = new MySqlUserDAO();
        AuthData a = dao.createUser(new UserData(username, password, email));
        dao.logout(a.authToken());
        assertThrows(DataAccessException.class, () -> dao.loginUser(username, "wrongPassword"),
                "Should throw DataAccessException for wrong password");
    }

    @Test
    public void testClearSuccess() throws Exception {
        MySqlClearDAO dao = new MySqlClearDAO();
        dao.clearData();
        try (Connection conn = DatabaseManager.getConnection()) {
            for (String table : new String[]{"auth", "users", "games"}) {
                try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM " + table);
                     ResultSet rs = ps.executeQuery()) {
                    assertFalse(rs.next(), table + " should be empty");
                }
            }
        }
    }

    @Test
    public void testClearFail() throws Exception {
        MySqlClearDAO dao = new MySqlClearDAO();
        dao.clearData();
        // This test seems incomplete - what failure case are you testing?
        // For now, just verifying clear works as above
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users");
             ResultSet rs = ps.executeQuery()) {
            assertFalse(rs.next(), "Users table should be empty");
        }
    }

    @Test
    public void testCreateGameSuccess() throws Exception {
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        MySqlUserDAO userDao = new MySqlUserDAO();
        AuthData a = userDao.createUser(new UserData(username, password, email));
        MySqlGameDAO gameDao = new MySqlGameDAO();
        gameDao.createGame("game name", a.authToken());
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM games WHERE gameName = ?")) {
            ps.setString(1, "game name");
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next(), "Game entry should exist");
            }
        }
    }

    @Test
    public void testCreateGameFail() throws Exception {
        String username = "testuser";
        String email = "test@example.com";
        String password = "password123";
        MySqlUserDAO userDao = new MySqlUserDAO();
        AuthData a = userDao.createUser(new UserData(username, password, email));
        MySqlGameDAO gameDao = new MySqlGameDAO();
        assertThrows(DataAccessException.class, () -> gameDao.createGame("game name", "bad authToken"),
                "Should throw DataAccessException for invalid auth token");
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
        assertThrows(DataAccessException.class, () -> gameDao.listGames("bad authToken"),
                "Should throw DataAccessException for invalid auth token");
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
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM games WHERE gameName = ? AND id = ?")) {
            ps.setString(1, "game name");
            ps.setInt(2, g.gameID());
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next(), "Game entry should exist");
                assertEquals(username, rs.getString("whiteUsername"));
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
        GameData g = gameDao.createGame("game name", a.authToken());
        assertThrows(DataAccessException.class, () -> gameDao.join("green", g.gameID(), a.authToken()),
                "Should throw DataAccessException for invalid color");
    }
}