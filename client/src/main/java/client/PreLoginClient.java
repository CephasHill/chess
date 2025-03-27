package client;

import model.AuthData;
import model.Pair;
import exception.ResponseException;
import model.request.LoginRequest;
import model.request.RegisterRequest;
import server.ServerFacade;

import java.util.Arrays;
import java.util.Objects;

public class PreLoginClient {
    private String username = null;
    private final ServerFacade server;
    private final String storageType = "sql";
    public PreLoginClient(int port) {
        server = new ServerFacade(port);
    }

    public Pair<String,AuthData> eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> new Pair<>("quit",null);
                default -> new Pair<>(help(),null);
            };
        } catch (Exception e) {
            return new Pair<>(e.getMessage(),null);
        }
    }
    public Pair<String,AuthData> register(String... params) throws ResponseException {
        if (params.length >= 3) {
            username = String.join("-", params[0]);
            try {
                var res = server.register(new RegisterRequest(params[0], params[1], params[2], storageType));
                return new Pair<>(String.format("logged in as %s", username),res);
            } catch (ResponseException e) {
                if (Objects.equals(e.getMessage(), "Cannot invoke \"java.lang.Double.intValue()\" because the return value of \"java.util.HashMap.get(Object)\" is null")) {
                    return new Pair<>("Username already exists",null);
                }
                return new Pair<>("Error: " + e.getMessage(),null);
            }
        }
        throw new ResponseException(400, "Excpected: <username password email>");
    }

    public Pair<String, AuthData> login(String... params) throws ResponseException {
        if (params.length >= 2) {
            username = String.join("-", params[0]);
            try {
                var res = server.login(new LoginRequest(params[0], params[1], storageType));
                return new Pair<>(String.format("logged in as %s", username), new AuthData(res.username(),res.authToken()));
            } catch (ResponseException e) {
                return new Pair<>("Error: " + e.getMessage(),null);
            }
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
