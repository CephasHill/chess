package service;

import dataaccess.DataAccessException;
import dataaccess.GameDataDAO;
import model.GameData;
import request.CreateGameRequest;
import request.JoinGameRequest;
import request.ListGamesRequest;
import result.CreateGameResult;
import result.JoinGameResult;
import result.ListGamesResult;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class GameService {
    public CreateGameResult createGame(String gameName, String authToken) throws DataAccessException {
        GameDataDAO dao = new GameDataDAO();
        GameData data = dao.createGame(gameName, authToken);
        return new CreateGameResult(data.gameID());
    }
    public ListGamesResult listGames(String authToken) throws DataAccessException {
        GameDataDAO dao = new GameDataDAO();
        ArrayList<GameData> gameListArray = dao.listGames(authToken);
        return new ListGamesResult(gameListArray);
    }
    public JoinGameResult joinGame(JoinGameRequest request) {
        return null;
    }
}
