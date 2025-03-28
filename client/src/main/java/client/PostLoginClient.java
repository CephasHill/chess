package client;

import exception.ResponseException;
import model.GameData;
import model.Pair;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.request.ListGamesRequest;
import model.request.LogoutRequest;
import server.ServerFacade;

import java.util.Arrays;

import static client.Repl.gameNumbers;

public class PostLoginClient {
    private final ServerFacade server;
    private final String storageType = "sql";
    private String auth = null;
    public PostLoginClient(int port) {
        server = new ServerFacade(port);
    }
    public Pair<String,GameData> eval(String input, String auth) {
        try {
            this.auth = auth;
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "logout" -> logout(params);
                case "list" -> listGames(params);
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
                case "observe" -> observeGame(params);
                case "quit" -> new Pair<>("quit",null);
                default -> help();
            };
        } catch (Exception e) {
            return new Pair<>(e.getMessage(),null);
        }
    }

    private Pair<String, GameData> observeGame(String[] params) {
        try {
            Integer.parseInt(params[0]);
        } catch (NumberFormatException e) {
            return new Pair<>("Game ID not valid.",null);
        }
        if (gameNumbers.contains(Integer.parseInt(params[0]))) {
            return new Pair<>("observing\n", null);
        }
        return new Pair<>("Game ID not valid.", null);
    }

    public Pair<String,GameData> logout(String... params) throws ResponseException {
        try {
            server.logout(new LogoutRequest(auth, storageType));
        } catch (ResponseException e) {
            return new Pair<>("Error: " + e.getMessage(),null);
        }
        return new Pair<>("logged out",null);
    }
    public Pair<String,GameData> listGames(String... params) throws ResponseException {
        try {
            var res = server.listGames(new ListGamesRequest(auth, storageType));
            StringBuilder list = new StringBuilder();
            list.append("list\n");
            int gamesNum = 0;
            for (GameData game : res.games()) {
                gamesNum++;
                list.append(Integer.toString(gamesNum) +
                        "Name: " + game.gameName() +
                        " || White Player: " + game.whiteUsername() +
                        " || Black Player: " + game.blackUsername() + "\n");
            }
            if (list.toString().isEmpty()) {
                return new Pair<>("No games found",null);
            } else {
                return new Pair<>(list.toString(),null);
            }
        } catch (ResponseException e) {
            return new Pair<>("Error: " + e.getMessage(),null);
        }
    }
    public Pair<String,GameData> createGame(String... params) throws ResponseException {
        try {
            server.createGame(new CreateGameRequest(params[0], auth, storageType));
            return new Pair<>(String.format("Created game %s", params[0]),null);
        } catch (ResponseException e) {
            return new Pair<>("Error: " + e.getMessage(),null);
        }
    }
    public Pair<String,GameData> joinGame(String... params) throws ResponseException {
        try {
            var data = server.joinGame(new JoinGameRequest(params[1],Integer.parseInt(params[0]),auth,storageType)).gameData();
            return new Pair<>(String.format("Joined game %s as color %s\n", params[0], params[1]),data);
        } catch (ResponseException e) {
            return new Pair<>("Error: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()),null);
        }
    }
    public Pair<String,GameData> help() {
        return new Pair<>("""
                The following are valid commands:
                - logout
                - list
                - create <gameName>
                - join <gameNumber>
                - observe <gameNumber>
                - quit
                """,null);
    }
}
