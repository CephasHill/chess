package handler;

import dataaccess.DataAccessException;
import model.GameData;
import request.ListGamesRequest;
import service.GameService;

import java.util.ArrayList;

public class ListHandler {
    public ArrayList<ArrayList<Object>> listGames(ListGamesRequest req) throws DataAccessException {
        GameService service = new GameService();
        ArrayList<ArrayList<Object>> list = new ArrayList<>();
        for (GameData g : service.listGames(req.authToken()).gameData()) {
            ArrayList<Object> gameDataList = new ArrayList<>();
            gameDataList.add(g.gameID());
            gameDataList.add(g.whiteUsername());
            gameDataList.add(g.blackUsername());
            gameDataList.add(g.gameName());

            list.add(gameDataList);
        }
        return list;
    }
}
