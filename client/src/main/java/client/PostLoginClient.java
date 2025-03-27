package client;

import exception.ResponseException;
import model.GameData;
import model.request.ListGamesRequest;
import model.request.LoginRequest;
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
//                case "create" -> createGame(params);
//                case "join" -> joinGame(params);
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
            for (GameData game : res.gamesList()) {
                list.append(STR."\{game.gameID()} Game Name: \{game.gameName()} - White Player: \{game.whiteUsername()} - Black Player: \{game.blackUsername()}");
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
    public String help() {
        return """
                The following are valid commands:
                - logout
                - list
                - create <gameName>
                - join <gameID>
                """;
    }
}
