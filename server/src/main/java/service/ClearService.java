package service;

import dataaccess.ClearDataDAO;
import request.DeleteRequest;

public class ClearService {
    public void clearAll(DeleteRequest request) {
        ClearDataDAO dao = new ClearDataDAO();
        dao.clearData();
    }
}
