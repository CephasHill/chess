import client.Repl;

public static void main(String[] args) {
    int port = 8080;
    if (args.length == 1) {
        port = Integer.parseInt(args[0]);
    }
    try {
        new Repl(port).run();
    } catch (Exception e) {
        e.printStackTrace();
    }
}