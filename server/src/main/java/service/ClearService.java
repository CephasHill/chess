package service;

import dataaccess.ClearDataDAO;
import request.DeleteRequest;

public class ClearService {
    public void clearAll(DeleteRequest request) {
        if (request == null) {
            throw new NullPointerException("request is null");
        }
        ClearDataDAO dao = new ClearDataDAO();
        dao.clearData();
    }
}
