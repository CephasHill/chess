package handler;

import dataaccess.DataAccessException;
import model.request.LogoutRequest;
import service.UserService;

public class LogoutHandler {
    public void logout(String authToken, String storageType) throws DataAccessException {
        UserService service = new UserService();
        service.logout(new LogoutRequest(authToken, storageType));
    }
}
