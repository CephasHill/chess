package handler;

import dataaccess.DataAccessException;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;

public class RegisterHandler {
    public RegisterResult register(String username, String password, String email) throws DataAccessException {

        UserService userService = new UserService();
        try {
            return userService.register(new RegisterRequest(username, password, email));
        }
        catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
