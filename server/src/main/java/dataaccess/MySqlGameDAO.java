package dataaccess;

import chess.ChessGame;
import model.GameData;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

import static server.Server.database;

public class MySqlGameDAO {
    public MySqlGameDAO() throws DataAccessException {
        configureDatabase();
    }

    public GameData createGame(String gameName, String authToken) throws DataAccessException {
        authorize(authToken);
        int id_ = generateID();
        ChessGame game = new ChessGame();
        GameData data = new GameData(id_, null, null, gameName, game);
        var statement = "INSERT INTO games (id, whiteUsername, blackUsername, gameName, game) VALUES (id_, null, null, gameName, game);";
        executeUpdate(statement);
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
    void authorize(String authToken) throws DataAccessException {
        if (!database.authMap.containsKey(authToken)) {
            throw new DataAccessException("Error: Unauthorized");
        }
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
    public void executeUpdate(String statement) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(statement)) {
                var rs = preparedStatement.executeQuery();
                rs.next();
                System.out.println(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS  games (
              `id` int NOT NULL AUTO_INCREMENT,
              `whiteUsername` varchar(256),
              `blackUsername` varchar(256),
              `gameName` varchar(256) NOT NULL,
              `game` ChessGame NOT NULL,
              PRIMARY KEY (`id`),
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };


    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}
