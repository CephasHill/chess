package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

import static dataaccess.DatabaseManager.authorize;
import static dataaccess.DatabaseManager.getConnection;
import static server.Server.database;

public class MySqlGameDAO {

    public GameData createGame(String gameName, String authToken) throws DataAccessException {
        authorize(authToken);
        Gson gson = new Gson();
        ChessGame game = new ChessGame();
        String json = gson.toJson(game);
        int generatedId = 0;
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            String statement = "INSERT INTO games (whiteUsername, blackUsername, gameName, chessGame) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(statement, Statement.RETURN_GENERATED_KEYS)) {
                ps.setNull(1, Types.VARCHAR);
                ps.setNull(2, Types.VARCHAR);
                ps.setString(3, gameName);
                ps.setObject(4, json);
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        generatedId = rs.getInt(1);
                    } else {
                        throw new DataAccessException("No generated ID returned");
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new DataAccessException("Failed to insert game: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
        return new GameData(generatedId, null, null, gameName, game);
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
        authorize(authToken);
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            throw new DataAccessException("Failed to join game: " + e.getMessage());
        }

    }
}
