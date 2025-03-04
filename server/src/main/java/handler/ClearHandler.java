package handler;

import request.DeleteRequest;
import service.ClearService;

public class ClearHandler {
    public void clearAll() {
        ClearService clearService = new ClearService();
        clearService.clearAll(new DeleteRequest());
    }
}
