package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.Random;
import java.util.UUID;

import static server.Server.database;

public class GameDataDAO {

    public GameData createGame(String gameName, String authToken) throws DataAccessException {
        authorize(authToken);
        int id = generateID();
        ChessGame game = new ChessGame();
        return new GameData(id, null, null, gameName, game);
    }
    void getGame(GameData g) throws DataAccessException {

    }
    void listGames(GameData g) throws DataAccessException {
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
