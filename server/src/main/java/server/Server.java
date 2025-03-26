package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.MemoryDatabase;
import handler.*;
import model.GameData;
import model.UserData;
import model.request.CreateGameRequest;
import model.request.JoinGameRequest;
import model.request.ListGamesRequest;
import model.result.CreateGameResult;
import model.result.JoinGameResult;
import model.result.LoginResult;
import model.result.RegisterResult;
import spark.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class Server {
    public String storageType = "sql"; // "mem" or "sql"

    public static MemoryDatabase database = new MemoryDatabase();

    public static MemoryDatabase getDatabase() {
        return database;
    }

    public static void setDatabase(MemoryDatabase database) {
        Server.database = database;
    }

    public int run(int desiredPort) {
        if (storageType.equals("sql")) {
            try {
                DatabaseManager.createDatabase();
                DatabaseManager.initializeDatabase();
            } catch (DataAccessException | SQLException e) {
                throw new RuntimeException("Failed to initialize database: " + e.getMessage());
            }
        }
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");
        Spark.delete("/db", this::clearDB);
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::createGame);
        Spark.get("/game", this::listGames);
        Spark.put("/game", this::joinGame);
        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object joinGame(Request request, Response response) {
        Gson gson = new Gson();
        String authToken = request.headers("Authorization");
        var body = request.body();
        JoinGameRequest temp = gson.fromJson(body, JoinGameRequest.class);
        JoinGameRequest joinRequest = new JoinGameRequest(temp.playerColor(), temp.gameID(), authToken, storageType);
        JoinHandler handler = new JoinHandler();
        try {
            handler.join(joinRequest);
            response.status(200);
            return gson.toJson(new JoinGameResult());
        } catch (DataAccessException e) {
            if (e.getMessage().equals("Error: Unavailable")) {
                response.status(403);
                return gson.toJson(Map.of("message", e.getMessage()));
            }
            else if (e.getMessage().equals("Error: Unacceptable color")) {
                response.status(400);
                return gson.toJson(Map.of("message", e.getMessage()));
            }
            else if (e.getMessage().equals("Error: Unacceptable gameID")) {
                response.status(400);
                return gson.toJson(Map.of("message", e.getMessage()));
            }
            else if (e.getMessage().equals("Error: Unauthorized")) {
                response.status(401);
                return gson.toJson(Map.of("message", e.getMessage()));
            }
            else {
                response.status(500);
                return gson.toJson(Map.of("message", e.getMessage()));
            }
        }
    }

    private Object listGames(Request request, Response response) {
        Gson gson = new Gson();
        String authToken = request.headers("Authorization");
        ListHandler handler = new ListHandler();
        ListGamesRequest req = new ListGamesRequest(authToken, storageType);
        try {
            ArrayList<GameData> result = handler.listGames(req);
            response.status(200);
            return gson.toJson(Map.of("games", result));
        } catch (DataAccessException e) {
            response.status(401);
            return gson.toJson(Map.of("message",e.getMessage()));
        }
    }

    private Object createGame(Request request, Response response) {
        Gson gson = new Gson();
        if (request.headers("Authorization").isEmpty() || request.body().isEmpty()) {
            response.status(400);
            return gson.toJson(Map.of("message", "Error: bad request"));
        }
        String authToken = request.headers("Authorization");
        var temp = gson.fromJson(request.body(), CreateGameRequest.class);
        CreateGameRequest req = new CreateGameRequest(temp.gameName(), authToken, storageType);
        CreateHandler handler = new CreateHandler();
        try {
            CreateGameResult result = handler.createGame(req);
            response.status(200);
            return gson.toJson(Map.of("gameID", result.gameID()));
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
            handler.logout(authToken, storageType);
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
            LoginResult result = handler.login(u, storageType);
            response.status(200);
            return gson.toJson(result);
        }
        catch (Exception e) {
            var message = e.getMessage();
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
        clearHandler.clearAll(storageType);
        response.status(200);
        return "";
    }

    private Object registerUser(Request request, Response response) {
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
            handlerResult = handler.register(u.username(),u.password(),u.email(), storageType);
        } catch (DataAccessException e) {
            response.status(403);
            Map<String,String> errorMap = Map.of("message", e.getMessage());
            return gson.toJson(errorMap);
        }
        response.status(200);
        return gson.toJson(handlerResult);
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
