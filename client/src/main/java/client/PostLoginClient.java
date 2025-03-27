package client;

import exception.ResponseException;
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
        if (params.length >= 2) {
            try {
                server.logout(new LogoutRequest(auth, storageType));
                state = State.SIGNEDOUT;
            } catch (ResponseException e) {
                return "Error: " + e.getMessage();
            }
            return "logged out";
        }
        throw new ResponseException(400, "Excpected: <username password>");
    }
    public String help() {
        return """
                The following are valid commands:
                - register <username, password, email>
                - login <username password>
                - help
                - quit
                """;
    }
}
