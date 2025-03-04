package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.Database;
import handler.ClearHandler;
import handler.RegisterHandler;
import model.AuthData;
import model.UserData;
import result.RegisterResult;
import spark.*;

import java.util.Map;

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
        Spark.post("/user", this::addUser);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object clearDB(Request request, Response response) {
        ClearHandler clearHandler = new ClearHandler();
        clearHandler.clearAll();
        response.status(200);
        return "";
    }

    private Object addUser (Request request, Response response) {
        Gson gson = new Gson();
        var u = gson.fromJson(request.body(), UserData.class);
        if (u.username() == null || u.password() == null || u.email() == null) {
            response.status(400);
            Map<String, String> errorMap = Map.of("Error", "Username, password, and email are all required");
            gson.toJson(errorMap);
            return gson.toJson(errorMap);
        }
        RegisterHandler handler = new RegisterHandler();
        RegisterResult handlerResult = null;
        try {
            handlerResult = handler.register(u.username(),u.password(),u.email());
        } catch (DataAccessException e) {
            response.status(403);
            Map<String,String> errorMap = Map.of("Error", e.getMessage());
            return gson.toJson(errorMap);
        }
        response.status(200);
        AuthData a = handlerResult.authData();
        return gson.toJson(a);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
