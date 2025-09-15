/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.dao;

import com.quanlycuahangxe.model.Sales;
import com.quanlycuahangxe.exception.SalesNotFoundException;
import com.quanlycuahangxe.exception.DuplicateSalesException;
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
public class SalesManagementDao {
    private final Connection conn;

    public SalesManagementDao() {
        this.conn = NewConnectPostgres.getConnection();
    }

    public Sales createSale(Sales sale) throws DuplicateSalesException {
        try {
            // Kiểm tra số lượng xe có đủ không
            if (!checkCarAvailability(sale.getCarId(), sale.getQuantity())) {
                throw new IllegalArgumentException("Số lượng xe không đủ để bán");
            }
            
            String sql = "INSERT INTO sales (car_id, customer_id, employee_id, sale_date, quantity, " +
                       "unit_price, total_amount, payment_method, status, created_at, updated_at) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING sale_id";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, sale.getCarId());
                ps.setInt(2, sale.getCustomerId());
                ps.setInt(3, sale.getEmployeeId());
                ps.setDate(4, Date.valueOf(sale.getSaleDate()));
                ps.setInt(5, sale.getQuantity());
                ps.setBigDecimal(6, sale.getUnitPrice());
                ps.setBigDecimal(7, sale.getTotalAmount());
                ps.setString(8, sale.getPaymentMethod());
                ps.setString(9, sale.getStatus());
                ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
                ps.setTimestamp(11, Timestamp.valueOf(LocalDateTime.now()));
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        sale.setSaleId(rs.getInt("sale_id"));
                    }
                }
            }
            return sale;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new DuplicateSalesException("Sale already exists: " + e.getMessage());
            }
            throw new RuntimeException("Lỗi khi thêm giao dịch bán hàng: " + e.getMessage(), e);
        }
    }

    public Sales getSaleById(int saleId) throws SalesNotFoundException {
        try {
            String sql = "SELECT * FROM sales WHERE sale_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, saleId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToSale(rs);
                    } else {
                        throw SalesNotFoundException.withId(saleId);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy giao dịch bán hàng: " + e.getMessage(), e);
        }
    }

    public Sales updateSale(int saleId, Sales sale) throws SalesNotFoundException {
        if (!existsById(saleId)) {
            throw SalesNotFoundException.withId(saleId);
        }
        
        try {
            String sql = "UPDATE sales SET car_id = ?, customer_id = ?, employee_id = ?, " +
                       "sale_date = ?, quantity = ?, unit_price = ?, total_amount = ?, " +
                       "payment_method = ?, status = ?, updated_at = ? WHERE sale_id = ?";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, sale.getCarId());
                ps.setInt(2, sale.getCustomerId());
                ps.setInt(3, sale.getEmployeeId());
                ps.setDate(4, Date.valueOf(sale.getSaleDate()));
                ps.setInt(5, sale.getQuantity());
                ps.setBigDecimal(6, sale.getUnitPrice());
                ps.setBigDecimal(7, sale.getTotalAmount());
                ps.setString(8, sale.getPaymentMethod());
                ps.setString(9, sale.getStatus());
                ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
                ps.setInt(11, saleId);
                
                ps.executeUpdate();
            }
            return getSaleById(saleId);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật giao dịch bán hàng: " + e.getMessage(), e);
        }
    }

    public void deleteSale(int saleId) throws SalesNotFoundException {
        if (!existsById(saleId)) {
            throw SalesNotFoundException.withId(saleId);
        }
        
        try {
            String sql = "DELETE FROM sales WHERE sale_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, saleId);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa giao dịch bán hàng: " + e.getMessage(), e);
        }
    }

    public List<Sales> getAllSales() {
        List<Sales> salesList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM sales ORDER BY sale_date DESC, sale_id DESC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    salesList.add(mapResultSetToSale(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách giao dịch bán hàng: " + e.getMessage(), e);
        }
        return salesList;
    }

    public List<Sales> getSalesWithPaging(int page, int size) {
        List<Sales> salesList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM sales ORDER BY sale_date DESC, sale_id DESC LIMIT ? OFFSET ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, size);
                ps.setInt(2, (page - 1) * size);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        salesList.add(mapResultSetToSale(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy giao dịch bán hàng phân trang: " + e.getMessage(), e);
        }
        return salesList;
    }

    public List<Sales> searchSalesByKeyword(String keyword) {
        List<Sales> salesList = new ArrayList<>();
        try {
            String sql = "SELECT s.* FROM sales s " +
                       "JOIN cars c ON s.car_id = c.car_id " +
                       "JOIN customers cust ON s.customer_id = cust.customer_id " +
                       "JOIN employees e ON s.employee_id = e.employee_id " +
                       "WHERE LOWER(c.name) LIKE ? OR LOWER(c.brand) LIKE ? " +
                       "OR LOWER(cust.name) LIKE ? OR LOWER(e.name) LIKE ? " +
                       "OR s.payment_method LIKE ? OR s.status LIKE ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                String searchPattern = "%" + keyword.toLowerCase() + "%";
                ps.setString(1, searchPattern);
                ps.setString(2, searchPattern);
                ps.setString(3, searchPattern);
                ps.setString(4, searchPattern);
                ps.setString(5, searchPattern);
                ps.setString(6, searchPattern);
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        salesList.add(mapResultSetToSale(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm kiếm giao dịch bán hàng: " + e.getMessage(), e);
        }
        return salesList;
    }

    public boolean existsById(int saleId) {
        try {
            String sql = "SELECT 1 FROM sales WHERE sale_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, saleId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public long countSales() {
        try {
            String sql = "SELECT COUNT(*) FROM sales";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đếm giao dịch bán hàng: " + e.getMessage(), e);
        }
        return 0;
    }

    public long countSalesByStatus(String status) {
        try {
            String sql = "SELECT COUNT(*) FROM sales WHERE status = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đếm giao dịch bán hàng theo trạng thái: " + e.getMessage(), e);
        }
        return 0;
    }

    public long countSalesByPaymentMethod(String paymentMethod) {
        try {
            String sql = "SELECT COUNT(*) FROM sales WHERE payment_method = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, paymentMethod);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đếm giao dịch bán hàng theo phương thức thanh toán: " + e.getMessage(), e);
        }
        return 0;
    }

    public Optional<Sales> findById(int saleId) {
        try {
            String sql = "SELECT * FROM sales WHERE sale_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, saleId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapResultSetToSale(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm giao dịch bán hàng: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Sales> findByCustomerId(int customerId) {
        List<Sales> salesList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM sales WHERE customer_id = ? ORDER BY sale_date DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, customerId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        salesList.add(mapResultSetToSale(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm giao dịch bán hàng theo khách hàng: " + e.getMessage(), e);
        }
        return salesList;
    }

    public List<Sales> findByEmployeeId(int employeeId) {
        List<Sales> salesList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM sales WHERE employee_id = ? ORDER BY sale_date DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, employeeId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        salesList.add(mapResultSetToSale(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm giao dịch bán hàng theo nhân viên: " + e.getMessage(), e);
        }
        return salesList;
    }

    public List<Sales> findByCarId(int carId) {
        List<Sales> salesList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM sales WHERE car_id = ? ORDER BY sale_date DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, carId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        salesList.add(mapResultSetToSale(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm giao dịch bán hàng theo xe: " + e.getMessage(), e);
        }
        return salesList;
    }

    public List<Sales> findByStatus(String status) {
        List<Sales> salesList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM sales WHERE status = ? ORDER BY sale_date DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        salesList.add(mapResultSetToSale(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm giao dịch bán hàng theo trạng thái: " + e.getMessage(), e);
        }
        return salesList;
    }

    public List<Sales> findByPaymentMethod(String paymentMethod) {
        List<Sales> salesList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM sales WHERE payment_method = ? ORDER BY sale_date DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, paymentMethod);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        salesList.add(mapResultSetToSale(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm giao dịch bán hàng theo phương thức thanh toán: " + e.getMessage(), e);
        }
        return salesList;
    }

    public List<Sales> findByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Sales> salesList = new ArrayList<>();
        try {
            String sql = "SELECT * FROM sales WHERE sale_date BETWEEN ? AND ? ORDER BY sale_date DESC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDate(1, Date.valueOf(startDate));
                ps.setDate(2, Date.valueOf(endDate));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        salesList.add(mapResultSetToSale(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm giao dịch bán hàng theo khoảng thời gian: " + e.getMessage(), e);
        }
        return salesList;
    }

    public double getTotalRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            String sql = "SELECT COALESCE(SUM(total_amount), 0) AS total_revenue " +
                       "FROM sales WHERE sale_date BETWEEN ? AND ? AND status = 'COMPLETED'";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDate(1, Date.valueOf(startDate));
                ps.setDate(2, Date.valueOf(endDate));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getDouble("total_revenue");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tính tổng doanh thu: " + e.getMessage(), e);
        }
        return 0.0;
    }

    public Map<String, Double> getMonthlyRevenue(int year) {
        Map<String, Double> monthlyRevenue = new HashMap<>();
        try {
            String sql = "SELECT EXTRACT(MONTH FROM sale_date) as month, SUM(total_amount) as revenue " +
                       "FROM sales WHERE EXTRACT(YEAR FROM sale_date) = ? AND status = 'COMPLETED' " +
                       "GROUP BY EXTRACT(MONTH FROM sale_date) ORDER BY month";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, year);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int month = rs.getInt("month");
                        double revenue = rs.getDouble("revenue");
                        monthlyRevenue.put(String.valueOf(month), revenue);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tính doanh thu theo tháng: " + e.getMessage(), e);
        }
        return monthlyRevenue;
    }

    public Map<Integer, Double> getTopSalesEmployees(int limit) {
        Map<Integer, Double> topEmployees = new HashMap<>();
        try {
            String sql = "SELECT employee_id, SUM(total_amount) as total_sales " +
                       "FROM sales WHERE status = 'COMPLETED' " +
                       "GROUP BY employee_id ORDER BY total_sales DESC LIMIT ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, limit);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int employeeId = rs.getInt("employee_id");
                        double totalSales = rs.getDouble("total_sales");
                        topEmployees.put(employeeId, totalSales);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy top nhân viên bán hàng: " + e.getMessage(), e);
        }
        return topEmployees;
    }

    public Map<Integer, Integer> getTopSellingCars(int limit) {
        Map<Integer, Integer> topCars = new HashMap<>();
        try {
            String sql = "SELECT car_id, SUM(quantity) as total_sold " +
                       "FROM sales WHERE status = 'COMPLETED' " +
                       "GROUP BY car_id ORDER BY total_sold DESC LIMIT ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, limit);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int carId = rs.getInt("car_id");
                        int totalSold = rs.getInt("total_sold");
                        topCars.put(carId, totalSold);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy top xe bán chạy: " + e.getMessage(), e);
        }
        return topCars;
    }

    public boolean checkCarAvailability(int carId, int quantity) {
        try {
            String sql = "SELECT quantity FROM cars WHERE car_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, carId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int availableQuantity = rs.getInt("quantity");
                        return availableQuantity >= quantity;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi kiểm tra số lượng xe: " + e.getMessage(), e);
        }
        return false;
    }

    public long countByCreatedAtYearAndMonth(int year, int month) {
        try {
            String sql = "SELECT COUNT(*) FROM sales WHERE EXTRACT(YEAR FROM created_at) = ? AND EXTRACT(MONTH FROM created_at) = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, year);
                ps.setInt(2, month);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đếm giao dịch bán hàng theo tháng: " + e.getMessage(), e);
        }
        return 0;
    }

    public Sales save(Sales sale) throws DuplicateSalesException {
        if (sale.getSaleId() == 0) {
            return createSale(sale);
        } else {
            try {
                return updateSale(sale.getSaleId(), sale);
            } catch (SalesNotFoundException e) {
                throw new RuntimeException("Không tìm thấy giao dịch bán hàng để cập nhật", e);
            }
        }
    }

    public void deleteById(int saleId) throws SalesNotFoundException {
        deleteSale(saleId);
    }

    public List<Sales> findAll() {
        return getAllSales();
    }

    public List<Sales> findAllWithPaging(int page, int size) {
        return getSalesWithPaging(page, size);
    }

    public long countByStatus(String status) {
        return countSalesByStatus(status);
    }

    public long countByPaymentMethod(String paymentMethod) {
        return countSalesByPaymentMethod(paymentMethod);
    }

    private Sales mapResultSetToSale(ResultSet rs) throws SQLException {
        Sales sale = new Sales();
        sale.setSaleId(rs.getInt("sale_id"));
        sale.setCarId(rs.getInt("car_id"));
        sale.setCustomerId(rs.getInt("customer_id"));
        sale.setEmployeeId(rs.getInt("employee_id"));
        sale.setSaleDate(rs.getDate("sale_date").toLocalDate());
        sale.setQuantity(rs.getInt("quantity"));
        sale.setUnitPrice(rs.getBigDecimal("unit_price"));
        sale.setTotalAmount(rs.getBigDecimal("total_amount"));
        sale.setPaymentMethod(rs.getString("payment_method"));
        sale.setStatus(rs.getString("status"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            sale.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            sale.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return sale;
    }
}