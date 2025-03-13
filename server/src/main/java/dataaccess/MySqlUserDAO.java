package dataaccess;

import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import javax.xml.crypto.Data;

import static dataaccess.DatabaseManager.authorize;
import static dataaccess.DatabaseManager.getConnection;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import static server.Server.database;

public class MySqlUserDAO {

    String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public AuthData createUser(UserData u) throws DataAccessException, SQLException {
        try {
            getUser(u.username());
        }
        catch (Exception e) {
            String authToken = generateAuth();
            try (Connection conn = getConnection()) {
                conn.setAutoCommit(false);
                try {
                    String insertUser = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insertUser)) {
                        ps.setString(1, u.username());
                        ps.setString(2, hashPassword(u.password()));
                        ps.setString(3, u.email());
                        ps.executeUpdate();
                        String insertAuth = "INSERT INTO auth (username, auth) VALUES (?, ?)";
                        try (PreparedStatement psAuth = conn.prepareStatement(insertAuth)) {
                            psAuth.setString(1, u.username());
                            psAuth.setString(2, authToken);
                            psAuth.executeUpdate();
                        }
                    }
                    conn.commit();
                } catch (Exception e2) {
                    conn.rollback();
                    if (e2 instanceof SQLException && ((SQLException) e2).getErrorCode() == 1062) {
                        throw new DataAccessException("Username or email already exists.");
                    }
                    throw new DataAccessException("Username or email already exists.");
                } finally {
                    conn.setAutoCommit(true);
                }
            }
            return new AuthData(u.username(), authToken);
        }
        throw new DataAccessException("Username already exists.");
    }

    public AuthData loginUser(String username, String password) throws DataAccessException {
        String authToken = generateAuth();
        try {
            getUser(username);
        } catch (Exception e) {
            throw new DataAccessException("Error: User not found");
        }
        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);
            try {
                String query = "SELECT password_hash FROM users WHERE username = ?";
                String storedHash;
                try (PreparedStatement ps = conn.prepareStatement(query)) {
                    ps.setString(1, username);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new DataAccessException("Error: User not found");
                        }
                        storedHash = rs.getString("password_hash");
                    }
                }
                if (!BCrypt.checkpw(password, storedHash)) {
                    throw new DataAccessException("Error: Wrong password");
                }
                String insertAuth = "REPLACE INTO auth (username, auth) VALUES (?, ?)";
                try (PreparedStatement psAuth = conn.prepareStatement(insertAuth)) {
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
            throw new DataAccessException("Database error.");
        }
    }

    private String generateAuth() {
        return UUID.randomUUID().toString();
    }

    public void getUser(String username) throws DataAccessException {
        String query = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new DataAccessException("Username already exists.");
                }
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
