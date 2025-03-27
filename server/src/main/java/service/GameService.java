package service;

import chess.ChessGame;
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
    public GameData joinGame(String color, int id, String authToken, String storageType) throws DataAccessException {
        if (storageType.equals("mem")) {
            MemoryGameDataDAO dao = new MemoryGameDataDAO();
            return dao.join(color, id, authToken);
        }
        else {
            MySqlGameDAO dao = new MySqlGameDAO();
            return dao.join(color, id, authToken);
        }
    }
}
