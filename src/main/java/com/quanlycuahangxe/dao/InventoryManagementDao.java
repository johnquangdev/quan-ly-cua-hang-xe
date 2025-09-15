/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.dao;

import com.quanlycuahangxe.model.Inventory;
import com.quanlycuahangxe.exception.InventoryNotFoundException;
import com.quanlycuahangxe.exception.DuplicateInventoryException;
import com.quanlycuahangxe.db.NewConnectPostgres;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 *
 * @author Minh
 */
public class InventoryManagementDao {
    private final Connection conn;

    public InventoryManagementDao() {
        this.conn = NewConnectPostgres.getConnection();
    }

    public Inventory createInventory(Inventory inventory) throws DuplicateInventoryException {
        try {
            if (existsByCarId(inventory.getCarId())) {
                throw DuplicateInventoryException.duplicateCarInventory(inventory.getCarId());
            }

            String sql = "INSERT INTO inventory (car_id, quantity, min_stock_level, max_stock_level, " +
                       "last_restocked_date, next_restock_date, status, notes, created_at, updated_at) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURN极ING inventory_id";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, inventory.getCarId());
                ps.setInt(2, inventory.getQuantity());
                ps.setInt(3, inventory.getMinStockLevel());
                ps.setInt(4, inventory.getMaxStockLevel());

                if (inventory.getLastRestockedDate() != null) {
                    ps.setDate(5, Date.valueOf(inventory.getLastRestockedDate()));
                } else {
                    ps.setNull(5, Types.DATE);
                }

                if (inventory.getNextRestockDate() != null) {
                    ps.setDate(6, Date.valueOf(inventory.getNextRestockDate()));
                } else {
                    ps.setNull(6, Types.DATE);
                }

                ps.setString(7, inventory.getStatus());
                ps.setString(8, inventory.getNotes());
                ps.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        inventory.setInventoryId(rs.getInt("inventory_id"));
                    }
                }
            }
            return inventory;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new DuplicateInventoryException("Inventory already exists: " + e.getMessage());
            }
            throw new RuntimeException("Lỗi khi thêm inventory record: " + e.getMessage(), e);
        }
    }

    public Inventory getInventoryById(int inventoryId) throws InventoryNotFoundException {
        try {
            String sql = "SELECT * FROM inventory WHERE inventory_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, inventoryId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToInventory(rs);
                    } else {
                        throw InventoryNotFoundException.withId(inventoryId);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy inventory record: " + e.getMessage(), e);
        }
    }

    public Inventory getInventoryByCarId(int carId) throws InventoryNotFoundException {
        try {
            String sql = "SELECT * FROM inventory WHERE car_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, carId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToInventory(rs);
                    } else {
                        throw InventoryNotFoundException.withCarId(carId);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy inventory record theo car ID: " + e.getMessage(), e);
        }
    }

    public List<Inventory> getAllInventory() {
        List<Inventory> inventoryList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM inventory ORDER BY inventory_id DESC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    inventoryList.add(mapResultSetToInventory(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách inventory: " + e.getMessage(), e);
        }
        return inventoryList;
    }

    public Inventory updateInventory(int inventoryId, Inventory inventory) throws InventoryNotFoundException {
        if (!existsById(inventoryId)) {
            throw InventoryNotFoundException.withId(inventoryId);
        }

        try {
            String sql = "UPDATE inventory SET car_id = ?, quantity = ?, min_stock_level = ?, " +
                       "max_stock_level = ?, last_restocked_date = ?, next_restock_date = ?, " +
                       "status = ?, notes = ?, updated_at = CURRENT_TIMESTAMP WHERE inventory_id = ?";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, inventory.getCarId());
                ps.setInt(2, inventory.getQuantity());
                ps.setInt(3, inventory.getMinStockLevel());
                ps.setInt(4, inventory.getMaxStockLevel());

                if (inventory.getLastRestockedDate() != null) {
                    ps.setDate(5, Date.valueOf(inventory.getLastRestockedDate()));
                } else {
                    ps.setNull(5, Types.DATE);
                }

                if (inventory.getNextRestockDate() != null) {
                    ps.setDate(6, Date.valueOf(inventory.getNextRestockDate()));
                } else {
                    ps.setNull(6, Types.DATE);
                }

                ps.setString(7, inventory.getStatus());
                ps.setString(8, inventory.getNotes());
                ps.setInt(9, inventoryId);

                int affectedRows = ps.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Cập nhật inventory thất bại, không có bản ghi nào bị ảnh hưởng");
                }
            }

            return getInventoryById(inventoryId);

        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật inventory record: " + e.getMessage(), e);
        }
    }

    public void deleteInventory(int inventoryId) throws InventoryNotFoundException {
        if (!existsById(inventoryId)) {
            throw InventoryNotFoundException.withId(inventoryId);
        }
        
        try {
            String sql = "DELETE FROM inventory WHERE inventory_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, inventoryId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa inventory record: " + e.getMessage(), e);
        }
    }

    public Inventory updateStockQuantity(int carId, int quantityChange) throws InventoryNotFoundException {
        try {
            Inventory inventory = getInventoryByCarId(carId);

            int newQuantity = inventory.getQuantity() + quantityChange;
            if (newQuantity < 0) {
                throw new IllegalArgumentException("Số lượng tồn kho không thể âm");
            }

            String sql = "UPDATE inventory SET quantity = ?, updated_at = ? WHERE car_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, newQuantity);
                ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                ps.setInt(3, carId);
                
                ps.executeUpdate();
            }

            return getInventoryByCarId(carId);
        } catch (InventoryNotFoundException e) {
            throw e;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật số lượng tồn kho: " + e.getMessage(), e);
        }
    }

    public boolean checkStockAvailability(int carId, int requiredQuantity) {
        try {
            Inventory inventory = getInventoryByCarId(carId);
            return inventory.getQuantity() >= requiredQuantity;
        } catch (InventoryNotFoundException e) {
            return false;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi kiểm tra số lượng tồn kho: " + e.getMessage(), e);
        }
    }

    public List<Inventory> getInventoryByStatus(String status) {
        List<Inventory> inventoryList = new ArrayList<>();
        try {
            String sql = "极SELECT * FROM inventory WHERE status = ? ORDER BY inventory_id DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        inventoryList.add(mapResultSetToInventory(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy inventory theo status: " + e.getMessage(), e);
        }
        return inventoryList;
    }

    public List<Inventory> getLowStockInventory() {
        List<Inventory> inventoryList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM inventory WHERE status = 'LOW_STOCK' ORDER BY quantity ASC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    inventoryList.add(mapResultSetToInventory(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi极 lấy inventory số lượng thấp: " + e.getMessage(), e);
        }
        return inventoryList;
    }

    public List<Inventory> getOutOfStockInventory() {
        List<Inventory> inventoryList = new ArrayList<>();
        String sql = "SELECT * FROM inventory WHERE status = ? ORDER BY inventory_id DESC";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "OUT_OF_STOCK");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    inventoryList.add(mapResultSetToInventory(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy inventory hết hàng: " + e.getMessage(), e);
        }
        return inventoryList;
    }

    public List<Inventory> getInventoryNeedRestock() {
        List<Inventory> inventoryList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM inventory WHERE status IN ('LOW_STOCK', 'OUT_OF_STOCK') " +
                       "OR (next_restock_date IS NOT NULL AND next_restock_date <= CURRENT_DATE + 7) " +
                       "ORDER BY next_restock_date ASC NULLS LAST";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    inventoryList.add(mapResultSetToInventory(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy inventory cần restock: " + e.getMessage(), e);
        }
        return inventoryList;
    }

    public List<Inventory> searchInventoryByKeyword(String keyword) {
        List<Inventory> inventoryList = new ArrayList<>();
        try {
            String sql = "SELECT i.* FROM inventory i " +
                       "JOIN cars c ON i.car_id = c.car_id " +
                       "WHERE LOWER(c.name) LIKE ? OR LOWER(c.brand) LIKE ? " +
                       "OR LOWER(c.model) LIKE ? OR LOWER(i.status) LIKE ? " +
                       "OR LOWER(i.notes) LIKE ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                String searchPattern = "%" + keyword.toLowerCase() + "%";
                ps.setString(1, searchPattern);
                ps.setString(2, searchPattern);
                ps.setString(3, searchPattern);
                ps.setString(4, searchPattern);
                ps.setString(5, searchPattern);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        inventoryList.add(mapResultSetToInventory(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm kiếm inventory: " + e.getMessage(), e);
        }
        return inventoryList;
    }

    public int getTotalInventoryQuantity() {
        try {
            String sql = "SELECT COALESCE(SUM(quantity), 0) AS total_quantity FROM inventory";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getInt("total_quantity");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tính tổng số lượng tồn kho: " + e.getMessage(), e);
        }
        return 0;
    }

    public double getTotalInventoryValue() {
        try {
            String sql = "SELECT COALESCE(SUM(i.quantity * c.price), 0) AS total_value " +
                       "FROM inventory i JOIN cars c ON i.car_id = c.car_id";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getDouble("total_value");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tính tổng giá trị tồn kho: " + e.getMessage(), e);
        }
        return 0.0;
    }

    public Map<String, Integer> getInventoryStatusStats() {
        Map<String, Integer> stats = new HashMap<>();
        try {
            String sql = "SELECT status, COUNT(*) AS count FROM inventory GROUP BY status";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    stats.put(rs.getString("status"), rs.getInt("count"));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy thống kê inventory status: " + e.getMessage(), e);
        }
        return stats;
    }

    public List<Inventory> getInventoryHistory(int carId, LocalDate startDate, LocalDate endDate) {
        List<Inventory> inventoryList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM inventory WHERE car_id = ? AND updated_at BETWEEN ? AND ? " +
                       "ORDER BY updated_at DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, carId);
                ps.setDate(2, Date.valueOf(startDate));
                ps.setDate(3, Date.valueOf(endDate));
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        inventoryList.add(mapResultSetToInventory(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy lịch sử inventory: " + e.getMessage(), e);
        }
        return inventoryList;
    }

    public boolean existsById(int inventoryId) {
        try {
            String sql = "SELECT 1 FROM inventory WHERE inventory_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, inventoryId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean existsByCarId(int carId) {
        try {
            String sql = "SELECT 1 FROM inventory WHERE car_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, carId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            return false;
        }
    }

   public Inventory save(Inventory inventory) throws DuplicateInventoryException {
        if (inventory.getInventoryId() == 0) {
            return createInventory(inventory);
        } else {
            try {
                return updateInventory(inventory.getInventoryId(), inventory);
            } catch (InventoryNotFoundException e) {
                throw new RuntimeException("Không tìm thấy inventory record để cập nhật", e);
            }
        }
    }

    public void deleteById(int inventoryId) throws InventoryNotFoundException {
        deleteInventory(inventoryId);
    }

    public List<Inventory> findAll() {
        return getAllInventory();
    }

    private Inventory mapResultSetToInventory(ResultSet rs) throws SQLException {
        Inventory inventory = new Inventory();
        inventory.setInventoryId(rs.getInt("inventory_id"));
        inventory.setCarId(rs.getInt("car_id"));
        inventory.setQuantity(rs.getInt("quantity"));
        inventory.setMinStockLevel(rs.getInt("min_stock_level"));
        inventory.setMaxStockLevel(rs.getInt("max_stock_level"));

        Date lastRestockedDate = rs.getDate("last_restocked_date");
        if (lastRestockedDate != null) {
            inventory.setLastRestockedDate(lastRestockedDate.toLocalDate());
        }

        Date nextRestockDate = rs.getDate("next_restock_date");
        if (nextRestockDate != null) {
            inventory.setNextRestockDate(nextRestockDate.toLocalDate());
        }

        inventory.setStatus(rs.getString("status"));
        inventory.setNotes(rs.getString("notes"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            inventory.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            inventory.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return inventory;
    }
}