package handler;

import dataaccess.DataAccessException;
import model.request.RegisterRequest;
import model.result.RegisterResult;
import service.UserService;

public class RegisterHandler {
    public RegisterResult register(String username, String password, String email, String storageType) throws DataAccessException {

        UserService userService = new UserService();
        try {
            return userService.register(new RegisterRequest(username, password, email, storageType));
        }
        catch (DataAccessException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
