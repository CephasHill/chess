package handler;

import com.google.gson.Gson;
import request.RegisterRequest;
import result.RegisterResult;
import service.UserService;

public class RegisterHandler {
    public RegisterResult register(String json) {
        var serializer = new Gson();
        RegisterRequest request = serializer.fromJson(json, RegisterRequest.class);
        UserService userService = new UserService();
        return userService.register(request);
    }
}
