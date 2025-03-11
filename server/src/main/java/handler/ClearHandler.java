package handler;

import request.DeleteRequest;
import service.ClearService;

public class ClearHandler {
    public void clearAll(String storageType) {
        ClearService clearService = new ClearService();
        clearService.clearAll(new DeleteRequest());
    }
}
