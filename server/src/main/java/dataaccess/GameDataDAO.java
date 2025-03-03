package dataaccess;

import model.GameData;

import java.util.HashMap;

public class GameDataDAO {
    private final HashMap<Integer, GameData> gameMap;

    public GameDataDAO(HashMap<Integer, GameData> gameMap) {
        this.gameMap = gameMap;
    }

    void createGame(GameData g) throws DataAccessException {
        gameMap.put(g.gameID(),g);
    }
    void getGame(GameData g) throws DataAccessException {
        gameMap.get(g.gameID());
    }
    void listGames(GameData g) throws DataAccessException {
    }
    void updateGame(GameData g) throws DataAccessException {
        gameMap.put(g.gameID(), g);
    }
    void clear() throws DataAccessException {
        gameMap.clear();
    }
}
