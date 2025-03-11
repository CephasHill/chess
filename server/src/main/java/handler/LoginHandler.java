package handler;

import dataaccess.DataAccessException;
import model.UserData;
import request.LoginRequest;
import result.LoginResult;
import service.UserService;

public class LoginHandler {
    public LoginResult login(UserData u, String storageType) throws DataAccessException {
        String username = u.username();
        String password = u.password();
        UserService service = new UserService();
        return service.login(new LoginRequest(username, password));
    }
}
