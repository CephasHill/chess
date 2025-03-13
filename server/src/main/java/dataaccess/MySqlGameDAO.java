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
        int id = generateID();
        ChessGame game = new ChessGame();
        var json = gson.toJson(game);
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                String statement = "INSERT INTO games (id, whiteUsername, blackUsername, gameName, chessGame) VALUES (?,?,?,?,?);";
                try (PreparedStatement ps = conn.prepareStatement(statement)) {
                    ps.setInt(1, id);
                    ps.setNull(2, Types.VARCHAR);
                    ps.setNull(3, Types.VARCHAR);
                    ps.setString(4, gameName);
                    ps.setObject(5, json);
                    ps.executeUpdate();
                }
            } catch (SQLException e) {
                throw new DataAccessException("Error: " + e.getMessage());
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error: " + e.getMessage());
        }
        GameData data = new GameData(id, null, null, gameName, game);
        return data;
    }
    public ArrayList<GameData> listGames(String authToken) throws DataAccessException {
        authorize(authToken);
        ArrayList<GameData> games = new ArrayList<>();
        for (int g : database.gameMap.keySet()) {
            games.add(database.gameMap.get(g));
        }
        return games;
    }
    private int generateID() {
        Random random = new Random();
        return 1000 + random.nextInt(9000);
    }

    public void join(String color, int id, String authToken) throws DataAccessException {
        authorize(authToken);
        if (!database.gameMap.containsKey(id)) {
            throw new DataAccessException("Error: Unacceptable gameID");
        }
        GameData ogData = database.gameMap.get(id);
        if (color == null) {
            throw new DataAccessException("Error: Unacceptable color");
        }
        if (color.equalsIgnoreCase("white")) {
            if (ogData.whiteUsername() == null) {
                String username = database.authMap.get(authToken);
                database.gameMap.put(id, new GameData(id, username, ogData.blackUsername(), ogData.gameName(), ogData.game()));
            }
            else {
                throw new DataAccessException("Error: Unavailable");
            }
        }
        else if (color.equalsIgnoreCase("black")) {
            if (ogData.blackUsername() == null) {
                String username = database.authMap.get(authToken);
                database.gameMap.put(id, new GameData(id, ogData.whiteUsername(), username, ogData.gameName(), ogData.game()));
            }
            else {
                throw new DataAccessException("Error: Unavailable");
            }
        }
        else {
            throw new DataAccessException("Error: Unacceptable color");
        }
    }
}
