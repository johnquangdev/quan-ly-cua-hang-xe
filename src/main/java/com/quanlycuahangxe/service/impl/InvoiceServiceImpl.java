package com.quanlycuahangxe.service.impl;

import com.quanlycuahangxe.dao.InvoiceDAO;
import com.quanlycuahangxe.db.NewConnectPostgres;
import com.quanlycuahangxe.model.Invoice;
import com.quanlycuahangxe.model.InvoiceItem;
import com.quanlycuahangxe.model.Product;
import com.quanlycuahangxe.service.interfaces.InventoryLogService;
import com.quanlycuahangxe.service.interfaces.InvoiceService;
import com.quanlycuahangxe.service.interfaces.ProductService;
import com.quanlycuahangxe.utils.ServiceResult;
import java.awt.Component;
import java.awt.Window;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import javax.swing.SwingUtilities;

public class InvoiceServiceImpl implements InvoiceService {

    private final ProductService productService;
    private final InventoryLogService inventoryLogService;
    private final DataSource dataSource;

    public InvoiceServiceImpl() {
        this.dataSource = NewConnectPostgres.getDataSource();
        this.productService = new ProductServiceImpl();
        this.inventoryLogService = new InventoryLogServiceImpl();
    }

    @Override
    public ServiceResult<Invoice> createInvoice(int customerId, int staffId) {
        try (Connection conn = dataSource.getConnection()) {
            return createInvoice(conn, customerId, staffId);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi khi tạo hóa đơn: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<Invoice> createInvoice(Connection conn, int customerId, int staffId)
            throws SQLException {
        InvoiceDAO invoiceDAO = new InvoiceDAO();
        Invoice inv = invoiceDAO.createInvoice(conn, customerId, staffId);
        if (inv == null) {
            return ServiceResult.failure("Tạo hóa đơn thất bại");
        }
        return ServiceResult.success(inv, "Tạo hóa đơn thành công");
    }

    @Override
    public ServiceResult<Invoice> getInvoiceById(int id) {
        try (Connection conn = dataSource.getConnection()) {
            return getInvoiceById(conn, id);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi khi lấy hóa đơn: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<Invoice> getInvoiceById(Connection conn, int id) throws SQLException {
        InvoiceDAO invoiceDAO = new InvoiceDAO();
        Invoice inv = invoiceDAO.getInvoiceById(conn, id);
        return inv != null ? ServiceResult.success(inv)
                : ServiceResult.failure("Không tìm thấy hóa đơn");
    }

    @Override
    public ServiceResult<List<Invoice>> getAllInvoices() {
        try (Connection conn = dataSource.getConnection()) {
            return getAllInvoices(conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi khi lấy danh sách hóa đơn: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<List<Invoice>> getAllInvoices(Connection conn) throws SQLException {
        InvoiceDAO invoiceDAO = new InvoiceDAO();
        List<Invoice> list = invoiceDAO.getAllInvoices(conn);
        return ServiceResult.success(list);
    }

    @Override
    public ServiceResult<InvoiceItem> addInvoiceItem(int invoiceId, int productId, int quantity,
            double price) {
        try (Connection conn = dataSource.getConnection()) {
            return addInvoiceItem(conn, invoiceId, productId, quantity, price);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi khi thêm item: " + e.getMessage());
        }
    }

    public ServiceResult<InvoiceItem> addInvoiceItem(Connection conn, int invoiceId, int productId,
            int quantity, double price) throws SQLException {
        InvoiceDAO invoiceDAO = new InvoiceDAO();
        // Kiểm tra số lượng tồn kho
        ServiceResult<Product> productResult = productService.getProductById(conn, productId);
        if (!productResult.isSuccess()) {
            return ServiceResult.failure("Không tìm thấy sản phẩm với ID: " + productId);
        }

        Product product = productResult.getData();
        if (product.getStockQuantity() < quantity) {
            return ServiceResult.failure("Số lượng tồn kho không đủ. Chỉ còn "
                    + product.getStockQuantity() + " sản phẩm.");
        }

        // Thêm item vào hóa đơn
        InvoiceItem it = invoiceDAO.addInvoiceItem(conn, invoiceId, productId, quantity, price);
        if (it == null) {
            return ServiceResult.failure("Không thêm được item vào hóa đơn");
        }

        // Cập nhật số lượng tồn kho
        ServiceResult<Product> updateStockResult
                = productService.updateStock(conn, productId, -quantity);
        if (!updateStockResult.isSuccess()) {
            invoiceDAO.removeInvoiceItem(conn, it.getId());
            return ServiceResult.failure("Thêm item thành công nhưng không thể cập nhật tồn kho: "
                    + updateStockResult.getMessage());
        }

        // Ghi log
        inventoryLogService.addLog(conn, productId, -quantity, "Bán hàng theo HĐ #" + invoiceId);

        return ServiceResult.success(it, "Đã thêm item và cập nhật tồn kho");
    }

    @Override
    public ServiceResult<Void> removeInvoiceItem(int invoiceItemId) {
        try (Connection conn = dataSource.getConnection()) {
            return removeInvoiceItem(conn, invoiceItemId);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi khi xóa item: " + e.getMessage());
        }
    }

    public ServiceResult<Void> removeInvoiceItem(Connection conn, int invoiceItemId)
            throws SQLException {
        InvoiceDAO invoiceDAO = new InvoiceDAO();
        boolean ok = invoiceDAO.removeInvoiceItem(conn, invoiceItemId);
        return ok ? ServiceResult.success(null, "Xóa item thành công")
                : ServiceResult.failure("Xóa item thất bại");
    }

    @Override
    public ServiceResult<Double> calculateTotal(int invoiceId) {
        try (Connection conn = dataSource.getConnection()) {
            return calculateTotal(conn, invoiceId);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi tính tổng: " + e.getMessage());
        }
    }

    public ServiceResult<Double> calculateTotal(Connection conn, int invoiceId)
            throws SQLException {
        InvoiceDAO invoiceDAO = new InvoiceDAO();
        double total = invoiceDAO.calculateTotal(conn, invoiceId);
        invoiceDAO.updateInvoiceTotal(conn, invoiceId, total);
        return ServiceResult.success(total, "Đã cập nhật tổng");
    }

    @Override
    public ServiceResult<Void> deleteInvoice(int id) {
        try (Connection conn = dataSource.getConnection()) {
            return deleteInvoice(conn, id);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi xóa hóa đơn: " + e.getMessage());
        }
    }

    @Override
    public ServiceResult<Void> deleteInvoice(Connection conn, int id) throws SQLException {
        InvoiceDAO invoiceDAO = new InvoiceDAO();
        // Lấy tất cả các item của hóa đơn để hoàn trả số lượng
        List<InvoiceItem> items = invoiceDAO.getInvoiceItems(conn, id);
        for (InvoiceItem item : items) {
            productService.updateStock(conn, item.getProductId(), item.getQuantity());
            inventoryLogService.addLog(conn, item.getProductId(), item.getQuantity(),
                    "Hoàn trả từ việc xóa HĐ #" + id);
        }

        // Xóa tất cả các item trước
        invoiceDAO.deleteInvoiceItemsByInvoiceId(conn, id);

        // Sau đó xóa hóa đơn
        boolean success = invoiceDAO.deleteInvoice(conn, id);
        if (success) {
            return ServiceResult.success(null, "Xóa hóa đơn thành công.");
        } else {
            return ServiceResult.failure("Xóa hóa đơn thất bại.");
        }
    }

    @Override
    public Window getWindowAncestor(Component component) {
        return SwingUtilities.getWindowAncestor(component);
    }

    @Override
    public ServiceResult<Invoice> getInvoiceDetails(int invoiceId) {
        try (Connection conn = dataSource.getConnection()) {
            return getInvoiceDetails(conn, invoiceId);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi lấy chi tiết hóa đơn: " + e.getMessage());
        }
    }

    public ServiceResult<Invoice> getInvoiceDetails(Connection conn, int invoiceId)
            throws SQLException {
        InvoiceDAO invoiceDAO = new InvoiceDAO();
        Invoice inv = invoiceDAO.getInvoiceDetails(conn, invoiceId);
        if (inv == null) {
            return ServiceResult.failure("Không tìm thấy hóa đơn");
        }
        return ServiceResult.success(inv);
    }

    @Override
    public ServiceResult<List<Invoice>> searchInvoicesByCustomerEmail(String email) {
        try (Connection conn = dataSource.getConnection()) {
            return searchInvoicesByCustomerEmail(conn, email);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi tìm kiếm hóa đơn: " + e.getMessage());
        }
    }

    public ServiceResult<List<Invoice>> searchInvoicesByCustomerEmail(Connection conn, String email)
            throws SQLException {
        InvoiceDAO invoiceDAO = new InvoiceDAO();
        List<Invoice> list = invoiceDAO.searchInvoicesByCustomerEmail(conn, email);
        return ServiceResult.success(list);
    }

    @Override
    public ServiceResult<List<Invoice>> getInvoicesByCustomerId(int customerId) {
        try (Connection conn = dataSource.getConnection()) {
            return getInvoicesByCustomerId(conn, customerId);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult
                    .failure("Lỗi SQL khi lấy hóa đơn theo khách hàng: " + e.getMessage());
        }
    }

    public ServiceResult<List<Invoice>> getInvoicesByCustomerId(Connection conn, int customerId)
            throws SQLException {
        InvoiceDAO invoiceDAO = new InvoiceDAO();
        List<Invoice> list = invoiceDAO.getInvoicesByCustomerId(conn, customerId);
        return ServiceResult.success(list);
    }

    @Override
    public ServiceResult<List<Invoice>> searchInvoices(String keyword) {
        try (Connection conn = dataSource.getConnection()) {
            return searchInvoices(conn, keyword);
        } catch (SQLException e) {
            e.printStackTrace();
            return ServiceResult.failure("Lỗi SQL khi tìm kiếm hóa đơn: " + e.getMessage());
        }
    }

    public ServiceResult<List<Invoice>> searchInvoices(Connection conn, String keyword)
            throws SQLException {
        InvoiceDAO invoiceDAO = new InvoiceDAO();
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllInvoices(conn);
        }

        return ServiceResult.success(invoiceDAO.searchInvoices(conn, keyword));
    }

    @Override
    public ServiceResult<Invoice> updateInvoice(int invoiceId, int customerId, int staffId,
            List<InvoiceItem> newItems) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            return updateInvoice(conn, invoiceId, customerId, staffId, newItems);
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return ServiceResult.failure("Lỗi SQL khi cập nhật hóa đơn: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public ServiceResult<Invoice> updateInvoice(Connection conn, int invoiceId, int customerId,
            int staffId, List<InvoiceItem> newItems) throws SQLException {
        conn.setAutoCommit(false);

        InvoiceDAO invoiceDAO = new InvoiceDAO();

        // 1. Lấy hóa đơn cũ để hoàn trả số lượng và ghi log
        ServiceResult<Invoice> oldInvoiceRes = getInvoiceDetails(conn, invoiceId);
        if (!oldInvoiceRes.isSuccess()) {
            conn.rollback();
            return ServiceResult.failure("Không tìm thấy hóa đơn cũ để cập nhật.");
        }
        Invoice oldInvoice = oldInvoiceRes.getData();

        // Hoàn trả số lượng sản phẩm cũ và ghi log
        if (oldInvoice.getItems() != null) {
            for (InvoiceItem item : oldInvoice.getItems()) {
                productService.updateStock(conn, item.getProductId(), item.getQuantity());
                inventoryLogService.addLog(conn, item.getProductId(), item.getQuantity(),
                        "Hoàn trả từ việc sửa HĐ #" + invoiceId);
            }
        }

        // 2. Cập nhật thông tin header của hóa đơn
        boolean updateHeaderSuccess
                = invoiceDAO.updateInvoiceHeader(conn, invoiceId, customerId, staffId);
        if (!updateHeaderSuccess) {
            conn.rollback();
            return ServiceResult.failure("Cập nhật thông tin hóa đơn thất bại.");
        }

        // 3. Xóa tất cả các item cũ của hóa đơn
        invoiceDAO.deleteInvoiceItemsByInvoiceId(conn, invoiceId);

        // 4. Thêm các item mới và cập nhật tồn kho, ghi log
        for (InvoiceItem newItem : newItems) {
            ServiceResult<Product> productResult
                    = productService.getProductById(conn, newItem.getProductId());
            if (!productResult.isSuccess()) {
                conn.rollback();
                return ServiceResult
                        .failure("Không tìm thấy sản phẩm với ID: " + newItem.getProductId());
            }

            Product product = productResult.getData();
            if (product.getStockQuantity() < newItem.getQuantity()) {
                conn.rollback();
                return ServiceResult
                        .failure("Số lượng tồn kho không đủ cho sản phẩm " + product.getName()
                                + ". Chỉ còn " + product.getStockQuantity() + " sản phẩm.");
            }

            InvoiceItem addedItem = invoiceDAO.addInvoiceItem(conn, invoiceId,
                    newItem.getProductId(), newItem.getQuantity(), newItem.getPrice());
            if (addedItem == null) {
                conn.rollback();
                return ServiceResult.failure("Không thêm được item mới vào hóa đơn.");
            }

            productService.updateStock(conn, newItem.getProductId(), -newItem.getQuantity());
            inventoryLogService.addLog(conn, newItem.getProductId(), -newItem.getQuantity(),
                    "Bán hàng theo HĐ #" + invoiceId);
        }

        // 5. Tính toán lại tổng tiền hóa đơn
        calculateTotal(conn, invoiceId);

        conn.commit();
        return ServiceResult.success(invoiceDAO.getInvoiceDetails(conn, invoiceId),
                "Cập nhật hóa đơn thành công.");
    }
}
