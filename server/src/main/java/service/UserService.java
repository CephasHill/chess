package service;

import model.UserData;
import request.LoginRequest;
import request.LogoutRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;

import java.util.UUID;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) {
        String authToken = UUID.randomUUID().toString();
        UserData userData = new UserData(authToken, registerRequest.username());
        return new RegisterResult(userData);
    }
    public LoginResult login(LoginRequest loginRequest) {
        return null;
    }
    void logout(LogoutRequest logoutRequest) {

    }
}
