package com.quanlycuahangxe.service.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.quanlycuahangxe.model.InventoryLog;
import com.quanlycuahangxe.utils.ServiceResult;

public interface InventoryLogService {

    /**
     * @param conn
     * @param productId 
     * @param changeAmount 
     * @param reason 
     * @return ServiceResult chứa bản ghi log đã tạo.
     * @throws java.sql.SQLException
     */
    
    ServiceResult<InventoryLog> addLog(Connection conn, int productId, int changeAmount, String reason) throws SQLException;

    /**
     * @param productId 
     * @return ServiceResult chứa danh sách các bản ghi log.
     * @throws java.sql.SQLException
     */
    ServiceResult<List<InventoryLog>> getLogsByProduct(int productId) throws SQLException;
}
