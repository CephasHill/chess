package model.result;

import model.GameData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public record ListGamesResult(ArrayList<GameData> gameData) implements Serializable {
}
