package server;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dataaccess.DataAccessException;
import dataaccess.Database;
import handler.*;
import model.AuthData;
import model.UserData;
import request.CreateGameRequest;
import result.CreateGameResult;
import result.LoginResult;
import result.RegisterResult;
import spark.*;

import java.util.Map;
import java.util.Objects;

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
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::createGame);

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object createGame(Request request, Response response) {
        Gson gson = new Gson();
        if (request.headers("Authorization").isEmpty() || request.body().isEmpty()) {
            response.status(400);
            return gson.toJson(Map.of("message", "Error: bad request"));
        }
        String authToken = request.headers("Authorization");
        var temp = gson.fromJson(request.body(), CreateGameRequest.class);
        CreateGameRequest req = new CreateGameRequest(temp.gameName(), authToken);
        CreateHandler handler = new CreateHandler();
        try {
            CreateGameResult result = handler.createGame(req);
            response.status(200);
            return gson.toJson(Map.of("gameID", result.id()));
        } catch (DataAccessException e) {
            response.status(401);
            return gson.toJson(Map.of("message", e.getMessage()));
        }
    }

    private Object logout(Request request, Response response) {
        Gson gson = new Gson();
        String authToken = request.headers("Authorization");
        LogoutHandler handler = new LogoutHandler();
        try {
            handler.logout(authToken);
        }
        catch (Exception e) {
            response.status(401);
            Map<String, String> errorMap = Map.of("message", e.getMessage());
            return gson.toJson(errorMap);
        }
        response.status(200);
        return "";
    }

    private Object login(Request request, Response response) {
        Gson gson = new Gson();
        var u = gson.fromJson(request.body(), UserData.class);
        if (u.username() == null || u.password() == null) {
            response.status(400);
            Map<String, String> errorMap = Map.of("message", "Error: Username and password are required");
            return gson.toJson(errorMap);
        }
        LoginHandler handler = new LoginHandler();
        try {
            LoginResult result = handler.login(u);
            AuthData a = new AuthData(result.username(),result.authToken());
            return gson.toJson(a);
        }
        catch (Exception e) {
            if (Objects.equals(e.getMessage(), "Error: Wrong password") || Objects.equals(e.getMessage(), "Error: User not found")) {
                response.status(401);
            }
            else {
                response.status(500);
            }
            Map<String, String> errorMap = Map.of("message", e.getMessage());
            return gson.toJson(errorMap);
        }
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
            Map<String, String> errorMap = Map.of("message", "Error: Username, password, and email are all required");
            return gson.toJson(errorMap);
        }
        RegisterHandler handler = new RegisterHandler();
        RegisterResult handlerResult = null;
        try {
            handlerResult = handler.register(u.username(),u.password(),u.email());
        } catch (DataAccessException e) {
            response.status(403);
            Map<String,String> errorMap = Map.of("message", e.getMessage());
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
