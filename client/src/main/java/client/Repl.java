package client;

import chess.ChessGame;
import client.websocket.NotificationHandler;
import client.websocket.WSFacade;
import exception.ResponseException;
import model.AuthData;
import model.GameData;
import websocket.messages.ServerMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static client.EscapeSequences.*;

public class Repl implements NotificationHandler {
    int port;
    WSFacade ws;
    private final PreLoginClient preLoginClient;
    private PostLoginClient postLoginClient;
    private final GameClient gameClient;
    private State state = State.SIGNEDOUT;
    private String auth = null;
    private String username = null;
    public static ArrayList<Integer> gameNumbers = new ArrayList<>();

    public Repl(int port) throws ResponseException {
        this.port = port;
        preLoginClient = new PreLoginClient(port);
        gameClient = new GameClient();
        ws = new WSFacade(port, this);
    }

    public void run() throws ResponseException {
        state = State.SIGNEDOUT;
        System.out.println("Welcome to chess. Enter \"help\" to start.");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        GameData gameData = null;
        while (!result.equals("quit")) {
            if (state == State.SIGNEDOUT) {
                printPrompt();
                String line = scanner.nextLine();
                try {
                    var pair = preLoginClient.eval(line);
                    result = pair.getLeft();
                    System.out.print(BLUE + result);
                    if (result.toLowerCase().startsWith("logged in as")) {
                        auth = pair.getRight().authToken();
                        username = pair.getRight().username();
                        state = State.SIGNEDIN;
                    }
                } catch (Exception e) {
                    System.out.println(RED + "Error: incorrect login or registration");
                }
            } else if (state == State.SIGNEDIN) {
                postLoginClient = new PostLoginClient(port, this, username);
                result = postLoginClient.eval("list", auth).getLeft();
                gameNumbers = (ArrayList<Integer>) extractGameNumbers(result);
                printPrompt();
                String line = scanner.nextLine();
                try {
                    var pair = postLoginClient.eval(line, auth);
                    result = pair.getLeft();
                    System.out.print(BLUE + result);
                    if (result.equalsIgnoreCase("logged out")) {
                        state = State.SIGNEDOUT;
                    }
                    if (result.toLowerCase().startsWith("joined game")) {
                        state = State.INGAME;
                        gameData = pair.getRight();
                        System.out.print(RESET);
                        gameClient.printBoard(gameData, new AuthData(username,auth));
                    }
                    if (result.toLowerCase().startsWith("observing\n")) {
                        state = State.INGAME;
                        gameData = pair.getRight();
                        System.out.print(RESET);
                        gameClient.printBoard(
                                new GameData(0,null,null,null,new ChessGame()),
                                new AuthData(username,auth));
                    }
                    if (result.toLowerCase().startsWith("list")) {
                        gameNumbers = (ArrayList<Integer>) extractGameNumbers(result);
                    }
                } catch (Exception e) {
                    System.out.print(RED + e);
                }
            } else if (state == State.INGAME) {
                printPrompt();
                String line = scanner.nextLine();
                try {
                    result = gameClient.eval(line, new AuthData(username,auth), gameData);
                    System.out.print(BLUE + result);
                } catch (Exception e) {
                    System.out.print(RED + e);
                }
            }
        }
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }
    public List<Integer> extractGameNumbers(String input) {
        ArrayList<Integer> numbers = new ArrayList<>();

        // Split the input into lines
        String[] lines = input.split("\n");

        // Process each line
        for (String line : lines) {
            // Trim whitespace and check if the line is non-empty
            line = line.trim();
            if (line.isEmpty()) {
                continue; // Skip empty lines
            }

            // Extract the number before "Name"
            int indexOfName = line.indexOf("Name");
            if (indexOfName != -1) {
                String prefix = line.substring(0, indexOfName).trim();
                try {
                    // Parse the first token as an integer
                    String[] tokens = prefix.split("\\s+");
                    if (tokens.length > 0) {
                        int number = Integer.parseInt(tokens[0]);
                        numbers.add(number);
                    }
                } catch (NumberFormatException e) {
                    // Skip lines where the prefix isn’t a valid number
                    continue;
                }
            }
        }

        return numbers;
    }

    @Override
    public void notify(ServerMessage notification) {
        switch (notification.getServerMessageType()) {
            case ServerMessage.ServerMessageType.ERROR:
                System.out.print(notification.getData());
            case ServerMessage.ServerMessageType.LOAD_GAME:
                System.out.print(notification.getData());
            case ServerMessage.ServerMessageType.NOTIFICATION:
                System.out.print(notification.getData());
            default:
                throw new IllegalStateException("Unexpected value: " + notification);
        }
    }
}
