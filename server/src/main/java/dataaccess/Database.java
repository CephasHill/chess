package dataaccess;

import model.GameData;

import java.util.HashMap;

public class Database {
    public HashMap<String, Pair<String, String>> userMap = new HashMap<>();
    public HashMap<String, String> authMap = new HashMap<>();
    public HashMap<Integer, GameData> gameMap = new HashMap<>();}
