package handler;

import chess.ChessGame;
import dataaccess.DataAccessException;
import model.GameData;
import model.request.JoinGameRequest;
import service.GameService;

public class JoinHandler {
    public GameData join(JoinGameRequest joinRequest) throws DataAccessException {
        String color = joinRequest.playerColor();
        int id = joinRequest.gameID();
        String authToken = joinRequest.authToken();
        String storageType = joinRequest.storageType();
        GameService service = new GameService();
        return service.joinGame(color, id, authToken, storageType);
    }
}
