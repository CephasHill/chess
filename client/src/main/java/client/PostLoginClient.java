package client;

import exception.ResponseException;
import model.GameData;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.request.ListGamesRequest;
import model.request.LogoutRequest;
import server.ServerFacade;

import java.util.Arrays;

public class PostLoginClient {
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private final String storageType = "sql";
    private String auth = null;
    public PostLoginClient(int port) {
        server = new ServerFacade(port);
    }
    public String eval(String input, String auth) {
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
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    public String logout(String... params) throws ResponseException {
        try {
            server.logout(new LogoutRequest(auth, storageType));
            state = State.SIGNEDOUT;
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
        return "logged out";
    }
    public String listGames(String... params) throws ResponseException {
        try {
            var res = server.listGames(new ListGamesRequest(auth, storageType));
            StringBuilder list = new StringBuilder();
            for (GameData game : res.games()) {
                list.append(STR."\{game.gameID()} Name: \{game.gameName()} || White Player: \{game.whiteUsername()} || Black Player: \{game.blackUsername()}\n");
            }
            if (list.toString().isEmpty()) {
                return "No games found";
            } else {
                return list.toString();
            }
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }
    public String createGame(String... params) throws ResponseException {
        try {
            server.createGame(new CreateGameRequest(params[0], auth, storageType));
            return String.format("Created game %s", params[0]);
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }
    public String joinGame(String... params) throws ResponseException {
        try {
            server.joinGame(new JoinGameRequest(params[1],Integer.parseInt(params[0]),auth,storageType));
            return String.format("Joined game %s as color %s", params[0], params[1]);
        } catch (ResponseException e) {
            return "Error: " + e.getMessage() + "\n" + Arrays.toString(e.getStackTrace());
        }
    }
    public String help() {
        return """
                The following are valid commands:
                - logout
                - list
                - create <gameName>
                - join <gameNumber>
                """;
    }
}
