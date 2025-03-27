package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Random;

import static server.Server.database;

public class MemoryGameDataDAO {

    public GameData createGame(String gameName, String authToken) throws DataAccessException {
        authorize(authToken);
        int id = generateID();
        ChessGame game = new ChessGame();
        GameData data = new GameData(id, null, null, gameName, game);
        database.gameMap.put(id, data);
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

    public GameData join(String color, int id, String authToken) throws DataAccessException {
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
                GameData gameData = new GameData(id, username, ogData.blackUsername(), ogData.gameName(), ogData.game());
                database.gameMap.put(id, gameData);
                return gameData;
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
        return null;
    }
}
