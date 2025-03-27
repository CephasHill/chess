package server;

import com.google.gson.Gson;
import exception.ResponseException;
import model.AuthData;
import model.request.*;
import model.result.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class ServerFacade {
    private final String serverUrl;
    private final Gson gson;

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
        this.gson = new Gson();
    }

    public AuthData register(RegisterRequest req) throws ResponseException {
        return makeRequest("POST", "/user", req, AuthData.class);
    }

    public LoginResult login(LoginRequest req) throws ResponseException {
        return makeRequest("POST", "/session", req, LoginResult.class);
    }

    public void logout(LogoutRequest req) throws ResponseException {
        makeRequest("DELETE", "/session", req, null);  // No response body expected
    }

    public CreateGameResult createGame(CreateGameRequest req) throws ResponseException {
        return makeRequest("POST", "/game", req, CreateGameResult.class);
    }

    public ListGamesResult listGames(ListGamesRequest req) throws ResponseException {
        return makeRequest("GET", "/game", req, ListGamesResult.class);
    }

    public JoinGameResult joinGame(JoinGameRequest req) throws ResponseException {
        return makeRequest("PUT", "/game", req, JoinGameResult.class);
    }

    public void clearDatabase(DeleteRequest req) throws ResponseException {
        makeRequest("DELETE", "/db", req, DeleteResult.class);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);

            String authToken = getAuthToken(request);
            if (authToken != null) {
                http.addRequestProperty("Authorization", authToken);
            }
            writeBody(method, request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(String method, Object request, HttpURLConnection http) throws IOException {
        if (request != null && ("POST".equals(method) || "PUT".equals(method))) {
            http.setDoOutput(true);
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private String getAuthToken(Object request) {
        if (request == null) return null;
        return switch (request) {
            case LogoutRequest r -> r.authToken();
            case CreateGameRequest r -> r.authorization();
            case ListGamesRequest r -> r.authToken();
            case JoinGameRequest r -> r.authToken();
            default -> null;
        };
    }
    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }
            throw new ResponseException(status, "failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        try (InputStream respBody = http.getInputStream()) {
            if (respBody != null && responseClass != null) {
                // Read the raw bytes and print them
                String rawResponse = new String(respBody.readAllBytes());
                response = new Gson().fromJson(rawResponse, responseClass);
            }
        }
        return response;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}