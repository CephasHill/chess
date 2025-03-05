package service;

import dataaccess.DataAccessException;
import dataaccess.UserDataDAO;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.LogoutResult;
import result.RegisterResult;

import java.util.UUID;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null) {
            throw new DataAccessException("Error: Username, password, and email are all required");
        }
        UserData userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        UserDataDAO dao = new UserDataDAO();
        try {
            dao.getUser(registerRequest.username());
        }
        catch (DataAccessException e) {
            AuthData authData = dao.createUser(userData);
            return new RegisterResult(authData);
        }
        throw new DataAccessException("Error: Username already exists");
    }
    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        UserDataDAO dao = new UserDataDAO();
        AuthData a = dao.loginUser(loginRequest.username(), loginRequest.password());
        return new LoginResult(a.username(),a.authToken());
    }
    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException {
        UserDataDAO dao = new UserDataDAO();
        try {
            dao.logout(logoutRequest.authToken());
        }
        catch (DataAccessException e) {
            throw new DataAccessException("Error: Invalid auth token");
        }
        return new LogoutResult();
    }
}
