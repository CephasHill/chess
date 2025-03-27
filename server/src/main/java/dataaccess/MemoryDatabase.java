package dataaccess;

import model.GameData;
import model.Pair;

import java.util.HashMap;

public class MemoryDatabase {
    public HashMap<String, Pair<String, String>> userMap = new HashMap<>();
    public HashMap<String, String> authMap = new HashMap<>();
    public HashMap<Integer, GameData> gameMap = new HashMap<>();}
