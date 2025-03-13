package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;

import static dataaccess.DatabaseManager.authorize;
import static dataaccess.DatabaseManager.getConnection;

public class MySqlGameDAO {

    public GameData createGame(String gameName, String authToken) throws DataAccessException {
        authorize(authToken); // Level 1
        Gson gson = new Gson();
        ChessGame game = new ChessGame();
        String json = gson.toJson(game);
        try (Connection conn = getConnection()) { // Level 2
            conn.setAutoCommit(false);
            try { // Level 3
                String statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?)";
                int generatedId = insertGameAndGetId(conn, statement, gameName, json); // Level 4 via helper
                conn.commit();
                return new GameData(generatedId, null, null, gameName, game);
            } catch (SQLException e) {
                conn.rollback();
                throw new DataAccessException("Failed to insert game: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
    }

    // Helper method to insert game and retrieve generated ID
    private int insertGameAndGetId(Connection conn, String statement, String gameName, String json) throws SQLException, DataAccessException {
        try (PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
            ps.setNull(1, Types.VARCHAR);
            ps.setNull(2, Types.VARCHAR);
            ps.setString(3, gameName);
            ps.setObject(4, json);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                throw new DataAccessException("No generated ID returned");
            }
        }
    }

    public ArrayList<GameData> listGames(String authToken) throws DataAccessException {
        authorize(authToken);
        ArrayList<GameData> games = new ArrayList<>();
        String query = "SELECT id, whiteUsername, blackUsername, gameName, chessGame FROM games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            Gson gson = new Gson();
            while (rs.next()) {
                int gameID = rs.getInt("id");
                String whiteUsername = rs.getString("whiteUsername");
                String blackUsername = rs.getString("blackUsername");
                String gameName = rs.getString("gameName");
                String json = rs.getString("chessGame");
                ChessGame game = gson.fromJson(json, ChessGame.class);
                games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to list games: " + e.getMessage());
        }
        return games;
    }

    public void join(String color, int id, String authToken) throws DataAccessException {
        String username = authorize(authToken); // Level 1
        try (Connection conn = getConnection()) { // Level 2
            conn.setAutoCommit(false);
            try { // Level 3
                // Check game and get current players
                String[] players = checkGameAvailability(conn, id, color);
                String whiteUsername = players[0];
                String blackUsername = players[1];

                // Determine update query
                String updateQuery = getUpdateQuery(color, whiteUsername, blackUsername);

                // Update the game
                try (PreparedStatement ps = conn.prepareStatement(updateQuery)) { // Level 4
                    ps.setString(1, username);
                    ps.setInt(2, id);
                    int rowsAffected = ps.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new DataAccessException("Error: No games found with that id.");
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new DataAccessException("Error: Failed to join game: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: Failed to join game: " + e.getMessage());
        }
    }

    // Helper method to check game availability
    private String[] checkGameAvailability(Connection conn, int id, String color) throws SQLException, DataAccessException {
        String selectQuery = "SELECT whiteUsername, blackUsername FROM games WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(selectQuery)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new DataAccessException("Error: Unacceptable gameID");
                }
                return new String[] { rs.getString("whiteUsername"), rs.getString("blackUsername") };
            }
        }
    }

    // Helper method to determine update query and validate color
    private String getUpdateQuery(String color, String whiteUsername, String blackUsername) throws DataAccessException {
        if ("white".equalsIgnoreCase(color)) {
            if (whiteUsername != null) {
                throw new DataAccessException("Error: Unavailable");
            }
            return "UPDATE games SET whiteUsername = ? WHERE id = ?";
        } else if ("black".equalsIgnoreCase(color)) {
            if (blackUsername != null) {
                throw new DataAccessException("Error: Unavailable");
            }
            return "UPDATE games SET blackUsername = ? WHERE id = ?";
        } else {
            throw new DataAccessException("Error: Unacceptable color");
        }
    }
}
