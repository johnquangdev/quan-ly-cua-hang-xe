package com.quanlycuahangxe.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.quanlycuahangxe.model.InventoryLog;

public class InventoryLogDAO {

    // CRUD Methods
    public boolean addLog(Connection conn, InventoryLog log) throws SQLException {
        String sql =
                "INSERT INTO inventory_logs (product_id, change_amount, reason, created_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, log.getProductId());
            stmt.setInt(2, log.getChangeAmount());
            stmt.setString(3, log.getReason());
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            return stmt.executeUpdate() > 0;
        }
    }

    // Data Retrieval Methods
    public List<InventoryLog> getLogsByProductId(Connection conn, int productId)
            throws SQLException {
        List<InventoryLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM inventory_logs WHERE product_id = ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                logs.add(mapResultSetToInventoryLog(rs));
            }
        }
        return logs;
    }

    // Helper Methods
    private InventoryLog mapResultSetToInventoryLog(ResultSet rs) throws SQLException {
        InventoryLog log = new InventoryLog();
        log.setId(rs.getInt("id"));
        log.setProductId(rs.getInt("product_id"));
        log.setChangeAmount(rs.getInt("change_amount"));
        log.setReason(rs.getString("reason"));
        log.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return log;
    }
}
