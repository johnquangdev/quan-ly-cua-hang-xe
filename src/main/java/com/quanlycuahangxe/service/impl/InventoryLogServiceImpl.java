package com.quanlycuahangxe.service.impl;

import com.quanlycuahangxe.dao.InventoryLogDAO;
import com.quanlycuahangxe.db.NewConnectPostgres;
import com.quanlycuahangxe.model.InventoryLog;
import com.quanlycuahangxe.service.interfaces.InventoryLogService;
import com.quanlycuahangxe.utils.ServiceResult;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;

public class InventoryLogServiceImpl implements InventoryLogService {

    private final DataSource dataSource;

    public InventoryLogServiceImpl() {
        this.dataSource = NewConnectPostgres.getDataSource();
    }

    public ServiceResult<InventoryLog> addLog(int productId, int changeAmount, String reason) {
        try (Connection conn = dataSource.getConnection()) {
            return addLog(conn, productId, changeAmount, reason);
        } catch (SQLException e) {
            return ServiceResult.failure("Lỗi SQL khi ghi log: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<InventoryLog> addLog(Connection conn, int productId, int changeAmount,
            String reason) throws SQLException {
        InventoryLogDAO inventoryLogDAO = new InventoryLogDAO();
        InventoryLog log = new InventoryLog(productId, changeAmount, reason);
        boolean success = inventoryLogDAO.addLog(conn, log);
        if (success) {
            return ServiceResult.success(log, "Ghi log thành công.");
        } else {
            return ServiceResult.failure("Không thể ghi log tồn kho.");
        }
    }

    @Override
    public ServiceResult<List<InventoryLog>> getLogsByProduct(int productId) {
        try (Connection conn = dataSource.getConnection()) {
            return getLogsByProduct(conn, productId);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi lấy lịch sử tồn kho: " + e.getMessage());
        }
    }

    public ServiceResult<List<InventoryLog>> getLogsByProduct(Connection conn, int productId)
            throws SQLException {
        InventoryLogDAO inventoryLogDAO = new InventoryLogDAO();
        List<InventoryLog> logs = inventoryLogDAO.getLogsByProductId(conn, productId);
        return ServiceResult.success(logs);
    }
}
