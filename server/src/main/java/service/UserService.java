package service;

import dataaccess.DataAccessException;
import dataaccess.Database;
import dataaccess.UserDataDAO;
import model.AuthData;
import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import java.util.UUID;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        AuthData authData = new AuthData(registerRequest.username(), authToken);
        UserData userData = new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email());
        UserDataDAO dao = new UserDataDAO();
        try {
            dao.getUser(userData);
        }
        catch (DataAccessException e) {
            dao.createUser(userData, authData);
            return new RegisterResult(authData);
        }
        throw new DataAccessException("Username already exists");
    }
    public LoginResult login(LoginRequest loginRequest) {
        return null;
    }
    void logout(LogoutRequest logoutRequest) {

    }
}
