package com.quanlycuahangxe.service.interfaces;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.quanlycuahangxe.model.InventoryLog;
import com.quanlycuahangxe.utils.ServiceResult;

public interface InventoryLogService {

    /**
     * @param conn
     * @param productId ID của sản phẩm
     * @param changeAmount Số lượng thay đổi (âm là xuất, dương là nhập)
     * @param reason Lý do thay đổi
     * @return ServiceResult chứa bản ghi log đã tạo.
     * @throws java.sql.SQLException
     */
    
    ServiceResult<InventoryLog> addLog(Connection conn, int productId, int changeAmount, String reason) throws SQLException;

    /**
     * @param productId ID của sản phẩm
     * @return ServiceResult chứa danh sách các bản ghi log.
     * @throws java.sql.SQLException
     */
    ServiceResult<List<InventoryLog>> getLogsByProduct(int productId) throws SQLException;
}
