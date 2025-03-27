package client;

import java.util.Scanner;

import static client.EscapeSequences.*;

public class Repl {
    private final PreLoginClient preLoginClient;
    private final PostLoginClient postLoginClient;
    private final GameClient gameClient;
    private State state = State.SIGNEDOUT;
    private String auth = null;

    public Repl(int port) {
        preLoginClient = new PreLoginClient(port);
        postLoginClient = new PostLoginClient(port);
        gameClient = new GameClient(port);
    }

    public void run() {
        state = State.SIGNEDOUT;
        System.out.println("Welcome to chess. Enter \"help\" to start.");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            if (state == State.SIGNEDOUT) {
                printPrompt();
                String line = scanner.nextLine();
                try {
                    var pair = preLoginClient.eval(line);
                    result = pair.getLeft();
                    auth = pair.getRight();
                    System.out.print(BLUE + result);
                    if (result.startsWith("logged in as")) {
                        state = State.SIGNEDIN;
                    }
                } catch (Exception e) {
                    System.out.println(RED + e);
                }
            } else if (state == State.SIGNEDIN) {
                printPrompt();
                String line = scanner.nextLine();
                try {
                    result = postLoginClient.eval(line, auth);
                    System.out.print(BLUE + result);
                    if (result.equals("logged out")) {
                        state = State.SIGNEDOUT;
                    }
                } catch (Exception e) {
                    System.out.print(RED + e);
                }
            } else if (state == State.INGAME) {
                printPrompt();
                String line = scanner.nextLine();
                try {
//                    result = gameClient.eval(line, auth);
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
}
