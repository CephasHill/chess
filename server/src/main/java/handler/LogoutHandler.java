package handler;

import dataaccess.DataAccessException;
import request.LogoutRequest;
import result.LogoutResult;
import service.UserService;

public class LogoutHandler {
    public LogoutResult logout(String authToken, String storageType) throws DataAccessException {
        UserService service = new UserService();
        return service.logout(new LogoutRequest(authToken));
    }
}
