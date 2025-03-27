package client;

import server.ServerFacade;

import java.util.Arrays;

public class GameClient {
    private final ServerFacade server;
    private State state = State.SIGNEDOUT;
    private final String storageType = "sql";
    private String auth = null;
    public GameClient(int port) {
        server = new ServerFacade(port);
    }
    public String eval(String input, String auth) {
        try {
            this.auth = auth;
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0].toLowerCase() : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
//                case "logout" -> logout(params);
//                case "list" -> listGames(params);
//                case "create" -> createGame(params);
//                case "join" -> joinGame(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception e) {
            return e.getMessage();
        }
    }
    public String help() {
        return """
                The following are valid commands:
                - help
                - quit
                """;
    }
}
