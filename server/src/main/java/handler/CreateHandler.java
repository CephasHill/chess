package handler;

import dataaccess.DataAccessException;
import model.request.CreateGameRequest;
import model.result.CreateGameResult;
import service.GameService;

public class CreateHandler {
    public CreateGameResult createGame(CreateGameRequest req) throws DataAccessException {
        String gameName = req.gameName();
        String authToken = req.authorization();
        GameService gameService = new GameService();
        return gameService.createGame(gameName, authToken, req.storageType());
    }
}
