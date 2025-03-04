package server;

import dataaccess.Database;
import handler.ClearHandler;
import spark.*;

public class Server {
    public static Database database = new Database();

    public static Database getDatabase() {
        return database;
    }

    public static void setDatabase(Database database) {
        Server.database = database;
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clearDB);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object clearDB(Request request, Response response) {
        ClearHandler clearHandler = new ClearHandler();
        clearHandler.clearAll();
        response.status(204);
        return "";
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
