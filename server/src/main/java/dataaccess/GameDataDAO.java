package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;
import java.util.Random;

import static server.Server.database;

public class GameDataDAO {

    public GameData createGame(String gameName, String authToken) throws DataAccessException {
        authorize(authToken);
        int id = generateID();
        ChessGame game = new ChessGame();
        GameData data = new GameData(id, null, null, gameName, game);
        database.gameMap.put(id, data);
        return data;
    }
    void getGame(GameData g) throws DataAccessException {

    }
    public ArrayList<GameData> listGames(String authToken) throws DataAccessException {
        authorize(authToken);
        ArrayList<GameData> games = new ArrayList<>();
        for (GameData g : database.gameMap.values()) {
            games.add(g.gameID(), g);
        }
        return games;
    }
    void updateGame(GameData g) throws DataAccessException {

    }
    void clear() throws DataAccessException {

    }
    private int generateID() {
        Random random = new Random();
        return 1000 + random.nextInt(9000);
    }
    void authorize(String authToken) throws DataAccessException {
        if (!database.authMap.containsValue(authToken)) {
            throw new DataAccessException("Error: Unauthorized");
        }
    }
}
