package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Database {
    public HashMap<String, Pair<String, String>> userMap = new HashMap<>();
    public HashMap<String, String> authMap = new HashMap<>();
    public HashMap<Integer, GameData> gameMap = new HashMap<>();

    public <T, E> T getKeyByValue(HashMap<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
