package client;

import model.Pair;
import exception.ResponseException;
import model.request.LoginRequest;
import model.request.RegisterRequest;
import server.ServerFacade;

import java.util.Arrays;

public class PreLoginClient {
    private String username = null;
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private final String storageType = "sql";
    public PreLoginClient(int port) {
        server = new ServerFacade(port);
    }

    public Pair<String,String> eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    public Pair<String,String> register(String... params) throws ResponseException {
        if (params.length >= 3) {
            username = String.join("-", params[0]);
            try {
                var res = server.register(new RegisterRequest(params[0], params[1], params[2], storageType));
                state = State.SIGNEDIN;
                return new Pair<>(String.format("You signed in as %s", username),res.authData().authToken());
            } catch (ResponseException e) {
                return new Pair<>("Error: " + e.getMessage(),null);
            }
        }
        throw new ResponseException(400, "Excpected: <username password email>");
    }

    public String login(String... params) throws ResponseException {
        if (params.length >= 2) {
            username = String.join("-", params[0]);
            try {
                server.login(new LoginRequest(params[0], params[1], storageType));
                state = State.SIGNEDIN;
            } catch (ResponseException e) {
                return "Error: " + e.getMessage();
            }
            return String.format("You logged in as %s", username);
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
