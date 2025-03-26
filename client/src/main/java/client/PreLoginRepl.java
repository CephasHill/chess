package client;

import java.util.Scanner;

import static client.EscapeSequences.*;

public class PreLoginRepl {
    private final PreLoginClient client;

    public PreLoginRepl(int port) {
        client = new PreLoginClient(port, this);
    }

    public void run() {
        System.out.println("Welcome to chess. Enter \"help\" to start.");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = client.eval(line);
            } catch (Exception e) {

            }
        }
    }

    private void printPrompt() {
        System.out.print("\n" + RESET + ">>> " + GREEN);
    }
}
