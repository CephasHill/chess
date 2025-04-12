package client;

import client.websocket.NotificationHandler;
import client.websocket.WSFacade;
import exception.ResponseException;
import model.AuthData;
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
    private final WSFacade ws;
    private final String storageType = "sql";
    private String auth = null;
    private String username;
    public PostLoginClient(int port, NotificationHandler notificationHandler, String username) throws ResponseException {
        server = new ServerFacade(port);
        ws = new WSFacade(port, notificationHandler);
        this.username = username;
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
                        " Name: " + game.gameName() +
                        " || White Player: " + game.whiteUsername() +
                        " || Black Player: " + game.blackUsername() + "\n");
            }
            if (list.toString().equals("list\n")) {
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
        String gameId;
        String playerColor;
        if (params.length < 2) {
            return new Pair<>("Error: Incorrect usage. (join <gameNumber> <playerColor>)",null);
        }
        try {
            gameId = params[0];
        } catch (Exception e) {
            return new Pair<>("Error: Incorrect usage. (join <gameNumber> <playerColor>)",null);
        }
        try {
            playerColor = params[1];
        } catch (Exception e) {
            return new Pair<>("Error: Incorrect usage. (join <gameNumber> <playerColor>)",null);
        }
        try {
            Integer.parseInt(gameId);
        } catch (NumberFormatException e) {
            return new Pair<>("Error: gameID must be type integer",null);
        }
        if (!playerColor.contentEquals("white") && !playerColor.contentEquals("black")) {
            return new Pair<>("Error: playerColor must be \"black\" or \"white\"",null);
        }
        if (!gameNumbers.contains(Integer.parseInt(gameId))) {
            return new Pair<>("Game ID not valid.",null);
        }
        try {
            var data = server.joinGame(new JoinGameRequest(playerColor,Integer.parseInt(gameId),auth,storageType)).gameData();
            ws.connect(data, new AuthData(username, auth));
            return new Pair<>(String.format("Joined game %s as color %s\n", gameId, playerColor),data);
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
