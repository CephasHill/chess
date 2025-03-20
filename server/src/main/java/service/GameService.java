package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryGameDataDAO;
import dataaccess.MySqlGameDAO;
import model.GameData;
import model.result.CreateGameResult;
import model.result.JoinGameResult;
import model.result.ListGamesResult;

import java.util.ArrayList;

public class GameService {
    public CreateGameResult createGame(String gameName, String authToken, String storageType) throws DataAccessException {
        if (storageType.equals("mem")) {
            MemoryGameDataDAO dao = new MemoryGameDataDAO();
            GameData data = dao.createGame(gameName, authToken);
            return new CreateGameResult(data.gameID());
        }
        else {
            MySqlGameDAO dao = new MySqlGameDAO();
            GameData data = dao.createGame(gameName, authToken);
            return new CreateGameResult(data.gameID());
        }
    }
    public ListGamesResult listGames(String authToken, String storageType) throws DataAccessException {
        if (storageType.equals("mem")) {
            MemoryGameDataDAO dao = new MemoryGameDataDAO();
            ArrayList<GameData> gameListArray = dao.listGames(authToken);
            return new ListGamesResult(gameListArray);
        }
        else {
            MySqlGameDAO dao = new MySqlGameDAO();
            ArrayList<GameData> gameListArray = dao.listGames(authToken);
            return new ListGamesResult(gameListArray);
        }
    }
    public JoinGameResult joinGame(String color, int id, String authToken, String storageType) throws DataAccessException {
        if (storageType.equals("mem")) {
            MemoryGameDataDAO dao = new MemoryGameDataDAO();
            dao.join(color, id, authToken);
            return new JoinGameResult();
        }
        else {
            MySqlGameDAO dao = new MySqlGameDAO();
            dao.join(color, id, authToken);
            return new JoinGameResult();
        }
    }
}
