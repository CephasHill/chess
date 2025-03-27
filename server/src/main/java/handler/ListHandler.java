package handler;

import dataaccess.DataAccessException;
import model.GameData;
import model.request.ListGamesRequest;
import service.GameService;

import java.util.ArrayList;

public class ListHandler {
    public ArrayList<GameData> listGames(ListGamesRequest req) throws DataAccessException {
        GameService service = new GameService();
        ArrayList<GameData> list = new ArrayList<>();
        for (GameData g : service.listGames(req.authToken(), req.storageType()).gamesList()) {
            GameData gameData = new GameData(g.gameID(), g.whiteUsername(), g.blackUsername(), g.gameName(),g.game());
            list.add(gameData);
        }
        return list;
    }
}
