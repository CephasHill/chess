package DataAccess;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.MySqlClearDAO;
import dataaccess.MySqlUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;

public class DataAccessTests {

    private static final String TEST_DB_URL = "jdbc:mysql://localhost:3306/chess_db";
    private static final String USER = "root";
    private static final String PASSWORD = "cs240"; // Your MySQL root password

    @BeforeAll
    public static void setupDatabase() throws SQLException {
        // Create a test database
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS chess_db_test");
            stmt.executeUpdate("USE chess_db_test");
            DatabaseManager.initializeDatabase(); // Assumes this creates tables
        }
    }

    @BeforeEach
    public void clearDatabase() throws SQLException {
        // Clear tables before each test
        try (Connection conn = DriverManager.getConnection(TEST_DB_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("DELETE FROM auth");
            stmt.executeUpdate("DELETE FROM users");
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
        try (Connection conn = DriverManager.getConnection(TEST_DB_URL, USER, PASSWORD);
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                assertTrue(rs.next(), "User should exist in users table");
                assertEquals(username, rs.getString("username"));
                assertEquals(email, rs.getString("email"));
            }
        }

        // Verify auth in database
        try (Connection conn = DriverManager.getConnection(TEST_DB_URL, USER, PASSWORD);
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
        try (Connection conn = DriverManager.getConnection(TEST_DB_URL, USER, PASSWORD);
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
        try (Connection conn = DriverManager.getConnection(TEST_DB_URL, USER, PASSWORD);
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
        try (Connection conn = DriverManager.getConnection(TEST_DB_URL, USER, PASSWORD)) {
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
}