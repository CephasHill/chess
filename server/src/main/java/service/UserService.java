package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryUserDataDAO;
import dataaccess.MySqlUserDAO;
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
        if (registerRequest.storageType().equals("mem")) {
            MemoryUserDataDAO dao = new MemoryUserDataDAO();
            try {
                dao.getUser(registerRequest.username());
            } catch (DataAccessException e) {
                AuthData authData = dao.createUser(userData);
                return new RegisterResult(authData);
            }
            throw new DataAccessException("Error: Username already exists");
        }
        else {
            MySqlUserDAO dao = new MySqlUserDAO();
            try {
                dao.getUser(registerRequest.username());
            } catch (DataAccessException e) {
                AuthData authData = dao.createUser(userData);
                return new RegisterResult(authData);
            }
            throw new DataAccessException("Error: Username already exists");
        }
    }
    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        if (loginRequest.storageType().equals("mem")) {
            MemoryUserDataDAO dao = new MemoryUserDataDAO();
            AuthData a = dao.loginUser(loginRequest.username(), loginRequest.password());
            return new LoginResult(a.username(), a.authToken());
        }
        else {
            MySqlUserDAO dao = new MySqlUserDAO();
            AuthData a = dao.loginUser(loginRequest.username(), loginRequest.password());
            return new LoginResult(a.username(), a.authToken());
        }
    }
    public LogoutResult logout(LogoutRequest logoutRequest) throws DataAccessException {
        if (logoutRequest.storageType().equals("mem")) {
            MemoryUserDataDAO dao = new MemoryUserDataDAO();
            try {
                dao.logout(logoutRequest.authToken());
            } catch (DataAccessException e) {
                throw new DataAccessException("Error: Invalid auth token");
            }
        }
        else {
            MySqlUserDAO dao = new MySqlUserDAO();
            try {
                dao.logout(logoutRequest.authToken());
            } catch (DataAccessException e) {
                throw new DataAccessException("Error: Invalid auth token");
            }
        }
        return new LogoutResult();
    }
}
