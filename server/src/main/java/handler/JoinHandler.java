package handler;

import dataaccess.DataAccessException;
import request.JoinGameRequest;
import service.GameService;

public class JoinHandler {
    public void join(JoinGameRequest joinRequest) throws DataAccessException {
        String color = joinRequest.playerColor();
        int id = joinRequest.gameID();
        String authToken = joinRequest.authToken();
        GameService service = new GameService();
        service.joinGame(color, id, authToken);
    }
}
