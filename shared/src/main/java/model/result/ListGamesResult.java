package model.result;

import model.GameData;

import java.io.Serializable;
import java.util.ArrayList;

public record ListGamesResult(ArrayList<GameData> games) implements Serializable {
}
