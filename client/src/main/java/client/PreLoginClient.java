package client;

import exception.ResponseException;
import model.request.LoginRequest;
import server.ServerFacade;

import java.util.Arrays;

public class PreLoginClient {
    private String username = null;
    private final ServerFacade server;
    private final int port;
    private State state = State.SIGNEDOUT;
    public PreLoginClient(int port, PreLoginRepl preLoginRepl) {
        server = new ServerFacade(port);
        this.port = port;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
//                case "login" -> login(params);
//                case "logout" -> logout(params);
//                case "list" -> listGames(params);
//                case "join" -> joinGame(params);
//                case "create" -> createGame(params);
                case "help" -> help();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    public String register(String... params) throws ResponseException {
        if (params.length >= 3) {
            username = String.join("-", params[0]);
            try {
                server.login(new LoginRequest(params[0], params[1], params[2]));
                state = state.SIGNEDIN;
            } catch (ResponseException e) {
                return e.getMessage();
            }
            return String.format("You signed in as %s", username);
        }
        throw new ResponseException(400, "Excpected: <username password email>");
    }

    public String help() {
        if (state == State.SIGNEDIN) {
            return """
                    - register <username, password, email>
                    - login <username password>
                    - help
                    - quit
                    """;
        }
        return """
                - list
                - logout
                - join
                - create
                - quit
                """;
    }

    private void assertSignedIn() throws ResponseException {
        if (state == State.SIGNEDOUT) {
            throw new ResponseException(400, "You must sign in");
        }
    }
}
