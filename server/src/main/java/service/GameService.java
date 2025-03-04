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

public class GameService {
    public CreateGameResult createGame(String gameName, String authToken) throws DataAccessException {
        GameDataDAO dao = new GameDataDAO();
        GameData data = dao.createGame(gameName, authToken);
        return new CreateGameResult(data.gameID());
    }
    public ListGamesResult listGames(ListGamesRequest request) {
        return null;
    }
    public JoinGameResult joinGame(JoinGameRequest request) {
        return null;
    }
}
