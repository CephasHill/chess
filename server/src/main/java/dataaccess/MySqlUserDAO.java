package dataaccess;

import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import static dataaccess.DatabaseManager.authorize;
import static dataaccess.DatabaseManager.getConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class MySqlUserDAO {

    String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public AuthData createUser(UserData u) throws DataAccessException {
        if (userExists(u.username())) { // Level 1
            throw new DataAccessException("Username already exists.");
        }
        String authToken = generateAuth();
        try (Connection conn = getConnection()) { // Level 2
            conn.setAutoCommit(false);
            try { // Level 3
                String insertUser = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insertUser)) { // Level 4
                    ps.setString(1, u.username());
                    ps.setString(2, hashPassword(u.password()));
                    ps.setString(3, u.email());
                    ps.executeUpdate();
                }
                String insertAuth = "INSERT INTO auth (username, auth) VALUES (?, ?)";
                try (PreparedStatement psAuth = conn.prepareStatement(insertAuth)) { // Level 4
                    psAuth.setString(1, u.username());
                    psAuth.setString(2, authToken);
                    psAuth.executeUpdate();
                }
                conn.commit();
                return new AuthData(u.username(), authToken);
            } catch (SQLException e) {
                conn.rollback();
                if (e.getErrorCode() == 1062) {
                    throw new DataAccessException("Username or email already exists.");
                }
                throw new DataAccessException("Failed to create user: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Connection error: " + e.getMessage());
        }
    }

    // Helper method to check if user exists
    private boolean userExists(String username) throws DataAccessException {
        String query = "SELECT username FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Database error: " + e.getMessage());
        }
    }

    public AuthData loginUser(String username, String password) throws DataAccessException {
        String authToken = generateAuth(); // Level 1
        try (Connection conn = DatabaseManager.getConnection()) { // Level 2
            conn.setAutoCommit(false);
            try { // Level 3
                UserData user = getUser(username); // Level 4 via getUser call
                if (!BCrypt.checkpw(password, user.password())) {
                    throw new DataAccessException("Error: Wrong password");
                }
                String insertAuth = "REPLACE INTO auth (username, auth) VALUES (?, ?)";
                try (PreparedStatement psAuth = conn.prepareStatement(insertAuth)) { // Level 4
                    psAuth.setString(1, username);
                    psAuth.setString(2, authToken);
                    psAuth.executeUpdate();
                }
                conn.commit();
                return new AuthData(username, authToken);
            } catch (SQLException e) {
                conn.rollback();
                throw new DataAccessException("Database error: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Connection error: " + e.getMessage());
        }
    }

    private String generateAuth() {
        return UUID.randomUUID().toString();
    }

    public UserData getUser(String username) throws DataAccessException {
        String query = "SELECT username, password_hash, email FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserData(rs.getString("username"), rs.getString("password_hash"), rs.getString("email"));
                }
                throw new DataAccessException("Error: User not found");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Database error: " + e.getMessage());
        }
    }

    public void logout(String authToken) throws DataAccessException {
        authorize(authToken);
        String statement = "DELETE FROM auth WHERE auth = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(statement)) {
            ps.setString(1, authToken);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
