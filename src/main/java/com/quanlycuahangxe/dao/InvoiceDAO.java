package com.quanlycuahangxe.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.quanlycuahangxe.model.Invoice;
import com.quanlycuahangxe.model.InvoiceItem;

public class InvoiceDAO {

    // CRUD Methods
    /**
     * Lấy chi tiết một hóa đơn bao gồm thông tin khách hàng, người dùng và các mặt hàng.
     *
     * @param conn Kết nối cơ sở dữ liệu.
     * @param invoiceId ID của hóa đơn cần lấy.
     * @return Đối tượng Invoice chứa đầy đủ thông tin, hoặc null nếu không tìm thấy.
     */
    public Invoice getInvoiceDetails(Connection conn, int invoiceId) {
        String sql = "SELECT i.id, i.created_at, i.total_amount, "
                + "c.id AS customer_id, c.full_name AS customer_name, "
                + "u.id AS user_id, u.full_name AS user_name " + "FROM invoices i "
                + "JOIN customers c ON i.customer_id = c.id " + "JOIN users u ON i.user_id = u.id "
                + "WHERE i.id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, invoiceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Invoice inv = new Invoice();
                    inv.setId(rs.getInt("id"));
                    inv.setCustomerId(rs.getInt("customer_id"));
                    inv.setCustomerName(rs.getString("customer_name"));
                    inv.setUserId(rs.getInt("user_id"));
                    inv.setUserName(rs.getString("user_name"));
                    inv.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    inv.setTotalAmount(rs.getDouble("total_amount"));

                    inv.setItems(getInvoiceItems(conn, invoiceId)); // Tải các mặt hàng của hóa đơn

                    return inv;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Ghi log lỗi chi tiết
            e.printStackTrace(); // Ghi log lỗi chi tiết
            e.printStackTrace(); // Ghi log lỗi chi tiết
            e.printStackTrace(); // Ghi log lỗi chi tiết
        }
        return null;
    }

    /**
     * Lấy danh sách các mặt hàng (items) của một hóa đơn cụ thể.
     *
     * @param conn Kết nối cơ sở dữ liệu.
     * @param invoiceId ID của hóa đơn.
     * @return Danh sách các InvoiceItem.
     */
    public List<InvoiceItem> getInvoiceItems(Connection conn, int invoiceId) {
        List<InvoiceItem> items = new ArrayList<>();
        String sql = "SELECT ii.id, ii.quantity, ii.price, "
                + "p.id AS product_id, p.name AS product_name " + "FROM invoice_items ii "
                + "JOIN products p ON ii.product_id = p.id " + "WHERE ii.invoice_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, invoiceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    InvoiceItem item = new InvoiceItem();
                    item.setId(rs.getInt("id"));
                    item.setInvoiceId(invoiceId);
                    item.setProductId(rs.getInt("product_id"));
                    item.setProductName(rs.getString("product_name"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPrice(rs.getDouble("price"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Ghi log lỗi
        }
        return items;
    }

    /**
     * Xóa tất cả các mặt hàng thuộc một hóa đơn.
     *
     * @param conn Kết nối cơ sở dữ liệu.
     * @param invoiceId ID của hóa đơn.
     * @return true nếu thành công, false nếu có lỗi.
     */
    public boolean deleteInvoiceItemsByInvoiceId(Connection conn, int invoiceId) {
        String sql = "DELETE FROM invoice_items WHERE invoice_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, invoiceId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace(); // Ghi log lỗi
            return false;
        }
    }

    /**
     * Xóa một hóa đơn khỏi cơ sở dữ liệu.
     *
     * @param conn Kết nối cơ sở dữ liệu.
     * @param id ID của hóa đơn cần xóa.
     * @return true nếu thành công, false nếu có lỗi.
     */
    public boolean deleteInvoice(Connection conn, int id) {
        String sql = "DELETE FROM invoices WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Ghi log lỗi
            return false;
        }
    }

    /**
     * Cập nhật thông tin header của hóa đơn (khách hàng và người dùng).
     *
     * @param conn Kết nối cơ sở dữ liệu.
     * @param invoiceId ID của hóa đơn.
     * @param customerId ID khách hàng mới.
     * @param userId ID người dùng mới.
     * @return true nếu thành công, false nếu có lỗi.
     */
    public boolean updateInvoiceHeader(Connection conn, int invoiceId, int customerId, int userId) {
        String sql = "UPDATE invoices SET customer_id = ?, user_id = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setInt(2, userId);
            ps.setInt(3, invoiceId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Ghi log lỗi
            return false;
        }
    }

    /**
     * Tạo một hóa đơn mới trong cơ sở dữ liệu.
     *
     * @param conn Kết nối cơ sở dữ liệu.
     * @param customerId ID của khách hàng.
     * @param userId ID của người dùng (nhân viên tạo hóa đơn).
     * @return Đối tượng Invoice mới được tạo với ID, hoặc null nếu có lỗi.
     */
    public Invoice createInvoice(Connection conn, int customerId, int userId) {
        String sql = "INSERT INTO invoices (customer_id, user_id, created_at, total_amount) "
                + "VALUES (?, ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setInt(2, userId);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setDouble(4, 0); // Tổng tiền ban đầu là 0
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                return new Invoice(id, customerId, userId, LocalDateTime.now(), 0);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Ghi log lỗi
        }
        return null;
    }

    /**
     * Lấy một hóa đơn theo ID.
     *
     * @param conn Kết nối cơ sở dữ liệu.
     * @param id ID của hóa đơn.
     * @return Đối tượng Invoice, hoặc null nếu không tìm thấy.
     */
    public Invoice getInvoiceById(Connection conn, int id) {
        String sql = "SELECT * FROM invoices WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Invoice(rs.getInt("id"), rs.getInt("customer_id"), rs.getInt("user_id"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getDouble("total_amount"));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Ghi log lỗi
        }
        return null;
    }

    // Data Retrieval Methods

    /**
     * Lấy tất cả các hóa đơn trong hệ thống.
     *
     * @param conn Kết nối cơ sở dữ liệu.
     * @return Danh sách các Invoice.
     */
    public List<Invoice> getAllInvoices(Connection conn) {
        String sql = "SELECT i.id, i.customer_id, i.user_id, i.created_at, i.total_amount, "
                + "c.full_name AS customer_name, u.full_name AS user_name " + "FROM invoices i "
                + "JOIN customers c ON i.customer_id = c.id " + "JOIN users u ON i.user_id = u.id "
                + "ORDER BY i.created_at DESC";
        List<Invoice> list = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Invoice inv = new Invoice();
                inv.setId(rs.getInt("id"));
                inv.setCustomerId(rs.getInt("customer_id"));
                inv.setCustomerName(rs.getString("customer_name"));
                inv.setUserId(rs.getInt("user_id"));
                inv.setUserName(rs.getString("user_name"));
                inv.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                inv.setTotalAmount(rs.getDouble("total_amount"));
                list.add(inv);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Ghi log lỗi chi tiết
        }
        return list;
    }

    /**
     * Lấy tất cả các hóa đơn của một khách hàng cụ thể.
     *
     * @param conn Kết nối cơ sở dữ liệu.
     * @param customerId ID của khách hàng.
     * @return Danh sách các Invoice của khách hàng đó.
     */
    public List<Invoice> getInvoicesByCustomerId(Connection conn, int customerId) {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.id, i.customer_id, i.user_id, i.created_at, i.total_amount, "
                + "c.full_name AS customer_name, u.full_name AS user_name " + "FROM invoices i "
                + "JOIN customers c ON i.customer_id = c.id " + "JOIN users u ON i.user_id = u.id "
                + "WHERE i.customer_id = ? " + "ORDER BY i.created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Invoice inv = new Invoice();
                inv.setId(rs.getInt("id"));
                inv.setCustomerId(rs.getInt("customer_id"));
                inv.setCustomerName(rs.getString("customer_name"));
                inv.setUserId(rs.getInt("user_id"));
                inv.setUserName(rs.getString("user_name"));
                inv.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                inv.setTotalAmount(rs.getDouble("total_amount"));
                list.add(inv);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Ghi log lỗi chi tiết
        }
        return list;
    }

    /**
     * Tìm kiếm hóa đơn theo email của khách hàng.
     *
     * @param conn Kết nối cơ sở dữ liệu.
     * @param email Email của khách hàng.
     * @return Danh sách các Invoice phù hợp.
     */
    public List<Invoice> searchInvoicesByCustomerEmail(Connection conn, String email) {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.id, i.customer_id, i.user_id, i.created_at, i.total_amount, "
                + "c.full_name AS customer_name, u.full_name AS user_name " + "FROM invoices i "
                + "JOIN customers c ON i.customer_id = c.id " + "JOIN users u ON i.user_id = u.id "
                + "WHERE c.email ILIKE ? " // ILIKE cho tìm kiếm không phân biệt hoa thường
                + "ORDER BY i.created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + email + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Invoice inv = new Invoice();
                inv.setId(rs.getInt("id"));
                inv.setCustomerId(rs.getInt("customer_id"));
                inv.setCustomerName(rs.getString("customer_name"));
                inv.setUserId(rs.getInt("user_id"));
                inv.setUserName(rs.getString("user_name"));
                inv.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                inv.setTotalAmount(rs.getDouble("total_amount"));
                list.add(inv);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Ghi log lỗi chi tiết
        }
        return list;
    }

    /**
     * Tìm kiếm hóa đơn theo từ khóa (tên khách hàng, tên nhân viên, ID hóa đơn).
     *
     * @param conn Kết nối cơ sở dữ liệu.
     * @param keyword Từ khóa tìm kiếm.
     * @return Danh sách các Invoice phù hợp.
     */
    public List<Invoice> searchInvoices(Connection conn, String keyword) {
        List<Invoice> list = new ArrayList<>();
        String sql = "SELECT i.id, i.customer_id, i.user_id, i.created_at, i.total_amount, "
                + "c.full_name AS customer_name, u.full_name AS user_name " + "FROM invoices i "
                + "JOIN customers c ON i.customer_id = c.id " + "JOIN users u ON i.user_id = u.id "
                + "WHERE c.full_name ILIKE ? OR u.full_name ILIKE ? OR CAST(i.id AS TEXT) ILIKE ? "
                + "ORDER BY i.created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Invoice inv = new Invoice();
                inv.setId(rs.getInt("id"));
                inv.setCustomerId(rs.getInt("customer_id"));
                inv.setCustomerName(rs.getString("customer_name"));
                inv.setUserId(rs.getInt("user_id"));
                inv.setUserName(rs.getString("user_name"));
                inv.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                inv.setTotalAmount(rs.getDouble("total_amount"));
                list.add(inv);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Ghi log lỗi chi tiết
        }
        return list;
    }

    /**
     * Thêm một sản phẩm vào chi tiết hóa đơn.
     *
     * @param conn Kết nối cơ sở dữ liệu.
     * @param invoiceId ID của hóa đơn.
     * @param productId ID của sản phẩm.
     * @param quantity Số lượng sản phẩm.
     * @param price Đơn giá của sản phẩm tại thời điểm thêm.
     * @return Đối tượng InvoiceItem mới được tạo, hoặc null nếu có lỗi.
     */
    public InvoiceItem addInvoiceItem(Connection conn, int invoiceId, int productId, int quantity,
            double price) {
        String sql = "INSERT INTO invoice_items (invoice_id, product_id, quantity, price) "
                + "VALUES (?, ?, ?, ?) RETURNING id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.setDouble(4, price);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int id = rs.getInt("id");
                return new InvoiceItem(id, invoiceId, productId, quantity, price);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Ghi log lỗi chi tiết
        }
        return null;
    }

    /**
     * Xóa một mặt hàng khỏi hóa đơn.
     *
     * @param conn Kết nối cơ sở dữ liệu.
     * @param invoiceItemId ID của mặt hàng cần xóa.
     * @return true nếu thành công, false nếu có lỗi.
     */
    public boolean removeInvoiceItem(Connection conn, int invoiceItemId) {
        String sql = "DELETE FROM invoice_items WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceItemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Ghi log lỗi chi tiết
            return false;
        }
    }

    /**
     * Tính tổng tiền của một hóa đơn dựa trên các mặt hàng.
     *
     * @param conn Kết nối cơ sở dữ liệu.
     * @param invoiceId ID của hóa đơn.
     * @return Tổng số tiền của hóa đơn.
     */
    public double calculateTotal(Connection conn, int invoiceId) {
        String sql =
                "SELECT SUM(quantity * price) AS total FROM invoice_items WHERE invoice_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Ghi log lỗi chi tiết
        }
        return 0;
    }

    /**
     * Cập nhật tổng tiền của hóa đơn trong cơ sở dữ liệu.
     *
     * @param conn Kết nối cơ sở dữ liệu.
     * @param invoiceId ID của hóa đơn.
     * @param total Tổng tiền mới.
     */
    public void updateInvoiceTotal(Connection conn, int invoiceId, double total) {
        String sql = "UPDATE invoices SET total_amount = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, total);
            ps.setInt(2, invoiceId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Ghi log lỗi chi tiết
        }
    }
}
