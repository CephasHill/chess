package dataaccess;

import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

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
        String authToken = generateAuth();
        try (Connection conn = DatabaseManager.getConnection()) {
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
            } catch (Exception e) {
                conn.rollback();
                if (e instanceof SQLException && ((SQLException) e).getErrorCode() == 1062) {
                    throw new DataAccessException("Username or email already exists.");
                }
                throw new DataAccessException("Username or email already exists.");
            } finally {
                conn.setAutoCommit(true);
            }
        }
        return new AuthData(u.username(), authToken);
    }

    public AuthData loginUser(String username, String password) throws DataAccessException {
        try {
            getUser(username);
        }
        catch (Exception e) {
            throw new DataAccessException(e.getMessage());
        }
        if (database.userMap.get(username).getLeft().equals(password)) {
            String auth = generateAuth();
            database.authMap.put(auth, username);
            return new AuthData(username,auth);
        }
        else {
            throw new DataAccessException("Error: Wrong password");
        }
    }

    private String generateAuth() {
        return UUID.randomUUID().toString();
    }

    public void getUser(String username) throws DataAccessException {
        String statement = "SELECT username, userData FROM users WHERE username = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.setString(1,username);
                var rs = preparedStatement.executeQuery(statement);
            }
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public void logout(String authToken) throws DataAccessException {
        if (!database.authMap.containsKey(authToken)) {
            throw new DataAccessException("Error: AuthToken not found");
        }
        database.authMap.remove(authToken);
    }
}
