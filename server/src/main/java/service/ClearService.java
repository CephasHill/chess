package service;

import dataaccess.MemoryClearDataDAO;
import request.DeleteRequest;

public class ClearService {
    public void clearAll(DeleteRequest request) {
        if (request == null) {
            throw new NullPointerException("request is null");
        }
        MemoryClearDataDAO dao = new MemoryClearDataDAO();
        dao.clearData();
    }
}
