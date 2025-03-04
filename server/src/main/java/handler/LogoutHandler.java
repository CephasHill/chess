package handler;

import dataaccess.DataAccessException;
import request.LogoutRequest;
import result.LogoutResult;
import service.UserService;

public class LogoutHandler {
    public LogoutResult logout(String authToken) throws DataAccessException {
        UserService service = new UserService();
        return service.logout(new LogoutRequest(authToken));
    }
}
