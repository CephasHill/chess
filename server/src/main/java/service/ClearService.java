package service;

import dataaccess.MemoryClearDataDAO;
import dataaccess.MySqlClearDAO;
import request.DeleteRequest;

public class ClearService {
    public void clearAll(DeleteRequest request) {
        if (request == null) {
            throw new NullPointerException("request is null");
        }
        if (request.storageType().equals("mem")) {
            MemoryClearDataDAO dao = new MemoryClearDataDAO();
            dao.clearData();
        }
        else {
            try {
                MySqlClearDAO dao = new MySqlClearDAO();
                dao.clearData();
            }
            catch (Exception e) {
                String message = e.getMessage();
                System.out.println(message);
            }
        }
    }
}
