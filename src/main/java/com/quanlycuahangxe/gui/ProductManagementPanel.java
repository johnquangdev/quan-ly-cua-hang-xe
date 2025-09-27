package com.quanlycuahangxe.gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import com.quanlycuahangxe.model.InventoryLog;
import com.quanlycuahangxe.model.Product;
import com.quanlycuahangxe.service.impl.BrandServiceImpl;
import com.quanlycuahangxe.service.impl.CategoryServiceImpl;
import com.quanlycuahangxe.service.impl.InventoryLogServiceImpl;
import com.quanlycuahangxe.service.impl.ProductServiceImpl;
import com.quanlycuahangxe.service.interfaces.BrandService;
import com.quanlycuahangxe.service.interfaces.CategoryService;
import com.quanlycuahangxe.service.interfaces.InventoryLogService;
import com.quanlycuahangxe.service.interfaces.ProductService;
import com.quanlycuahangxe.utils.ServiceResult;

public class ProductManagementPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAddProduct, btnEditProduct, btnDeleteProduct;
    private JButton btnAddCategory, btnAddBrand, btnImportStock, btnViewHistory;
    private JTextField txtSearch; 
    private JButton btnSearch; 
    private JButton btnRefresh; 

    private ProductService productService = new ProductServiceImpl();
    private InventoryLogService inventoryLogService = new InventoryLogServiceImpl();
    private CategoryService categoryService = new CategoryServiceImpl();
    private BrandService brandService = new BrandServiceImpl();

    public ProductManagementPanel() {
        setLayout(new BorderLayout(10, 10)); 

        // Panel tìm kiếm
        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(300, 30));
        btnSearch = new JButton("Tìm kiếm");
        btnSearch.setIcon(com.quanlycuahangxe.utils.IconHelper.loadIcon("search.png")); 

        btnRefresh = new JButton("Làm mới");
        btnRefresh.setIcon(com.quanlycuahangxe.utils.IconHelper.loadIcon("refresh.png")); 

        panelSearch.add(new JLabel("Tìm kiếm:"));
        panelSearch.add(txtSearch);
        panelSearch.add(btnSearch);
        panelSearch.add(btnRefresh);
        add(panelSearch, BorderLayout.NORTH);

        //  Table 
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Tên", "Brand", "Category", "Giá", "Stock", "Mô tả"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        CustomTableCellRenderer renderer = new CustomTableCellRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        table.getColumnModel().getColumn(4).setCellRenderer(new CurrencyRenderer());

        //  Buttons 
        JPanel panelBtn = new JPanel(new GridLayout(1, 0, 10, 0));
        btnAddProduct = new JButton("Thêm sản phẩm");
        btnAddProduct.setIcon(com.quanlycuahangxe.utils.IconHelper.loadIcon("add.png"));
        btnEditProduct = new JButton("Sửa sản phẩm");
        btnEditProduct.setIcon(com.quanlycuahangxe.utils.IconHelper.loadIcon("edit.png"));
        btnDeleteProduct = new JButton("Xóa sản phẩm");
        btnDeleteProduct.setIcon(com.quanlycuahangxe.utils.IconHelper.loadIcon("delete.png"));
        btnImportStock = new JButton("Nhập kho");
        btnImportStock.setIcon(com.quanlycuahangxe.utils.IconHelper.loadIcon("import.png"));
        btnViewHistory = new JButton("Xem lịch sử");
        btnViewHistory.setIcon(com.quanlycuahangxe.utils.IconHelper.loadIcon("history.png"));

        panelBtn.add(btnAddProduct);
        panelBtn.add(btnEditProduct);
        panelBtn.add(btnDeleteProduct);
        panelBtn.add(btnImportStock);
        panelBtn.add(btnViewHistory);

        // Panel cho Category và Brand, tách riêng ra cho gọn
        JPanel panelExtra = new JPanel(new GridLayout(1, 0, 10, 0));
        btnAddCategory = new JButton("Quản lý Category");
        btnAddCategory.setIcon(com.quanlycuahangxe.utils.IconHelper.loadIcon("category.png"));
        btnAddBrand = new JButton("Quản lý Brand");
        btnAddBrand.setIcon(com.quanlycuahangxe.utils.IconHelper.loadIcon("brand.png"));
        panelExtra.add(btnAddCategory);
        panelExtra.add(btnAddBrand);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(panelBtn, BorderLayout.CENTER);
        southPanel.add(panelExtra, BorderLayout.SOUTH);

        add(southPanel, BorderLayout.SOUTH);

        //  Load dữ liệu 
        loadProductData();

        //  Action listeners 
        btnAddProduct.addActionListener(
                e -> new ProductManagementForm(SwingUtilities.getWindowAncestor(this), null, this)
                        .setVisible(true));

        btnEditProduct.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Chọn sản phẩm để sửa");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            ServiceResult<Product> res = productService.getProductById(id);
            if (res.isSuccess()) {
                new ProductManagementForm(SwingUtilities.getWindowAncestor(this), res.getData(),
                        this).setVisible(true);
            }
        });

        btnDeleteProduct.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Chọn sản phẩm để xóa");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            int conf = JOptionPane.showConfirmDialog(this, "Bạn chắc chắn xóa sản phẩm này?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                ServiceResult<Void> res = productService.deleteProduct(id);
                JOptionPane.showMessageDialog(this, res.getMessage());
                loadProductData();
            }
        });

        btnAddCategory.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Tên Category mới:");
            if (name != null && !name.isBlank()) {
                categoryService.createCategory(name, "");
                JOptionPane.showMessageDialog(this, "Thêm Category thành công!");
            }
        });

        btnAddBrand.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Tên Brand mới:");
            if (name != null && !name.isBlank()) {
                brandService.createBrand(name, "");
                JOptionPane.showMessageDialog(this, "Thêm Brand thành công!");
            }
        });

        btnImportStock.addActionListener(e -> handleImportStock());
        btnViewHistory.addActionListener(e -> handleViewHistory());

        // Action listeners for search and refresh
        btnSearch.addActionListener(e -> loadProductData(txtSearch.getText()));
        btnRefresh.addActionListener(e -> {
            txtSearch.setText(""); 
            loadProductData(null);
        });
    }

    private void handleImportStock() {
        ImportStockDialog dialog = new ImportStockDialog(SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
        if (dialog.isSaved()) {
            loadProductData(); 
        }
    }

    private void handleViewHistory() {
        try {
            int row = table.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Chọn một sản phẩm để xem lịch sử.");
                return;
            }
            int productId = (int) tableModel.getValueAt(row, 0);
            String productName = (String) tableModel.getValueAt(row, 1);

            ServiceResult<List<InventoryLog>> result = inventoryLogService.getLogsByProduct(productId);
            if (result.isSuccess()) {
                List<InventoryLog> logs = result.getData();

                // Tạo bảng để hiển thị lịch sử
                String[] columnNames = {"Thời gian", "Thay đổi", "Lý do"};
                DefaultTableModel historyModel = new DefaultTableModel(columnNames, 0);
                for (InventoryLog log : logs) {
                    historyModel.addRow(new Object[]{
                        log.getCreatedAt(), log.getChangeAmount(), log.getReason()
                    });
                }
                JTable historyTable = new JTable(historyModel);

                // Apply custom renderer for zebra striping to historyTable
                CustomTableCellRenderer historyRenderer = new CustomTableCellRenderer();
                for (int i = 0; i < historyTable.getColumnCount(); i++) {
                    historyTable.getColumnModel().getColumn(i).setCellRenderer(historyRenderer);
                }

                // Hiển thị trong một dialog mới
                JDialog historyDialog = new JDialog(
                        SwingUtilities.getWindowAncestor(this),
                        "Lịch sử tồn kho - " + productName,
                        Dialog.ModalityType.APPLICATION_MODAL
                );
                historyDialog.add(new JScrollPane(historyTable));
                historyDialog.setSize(500, 300);
                historyDialog.setLocationRelativeTo(this);
                historyDialog.setVisible(true);

            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể tải lịch sử: " + result.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi cơ sở dữ liệu: " + ex.getMessage(),
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
            );
            ex.printStackTrace();
        }
    }

    public void loadProductData() {
        loadProductData(null); 
    }

    public void loadProductData(String keyword) {
        tableModel.setRowCount(0);
        ServiceResult<List<Product>> res;

        if (keyword == null || keyword.trim().isEmpty()) {
            res = productService.getAllProducts();
        } else {
            res = productService.searchProducts(keyword.trim());
        }

        if (res.isSuccess()) {
            for (Product p : res.getData()) {
                String brandName = brandService.getBrandById(p.getBrandId()).getData().getName();
                String categoryName
                        = categoryService.getCategoryById(p.getCategoryId()).getData().getName();
                tableModel.addRow(new Object[]{p.getId(), p.getName(), brandName, categoryName,
                    p.getPrice(), p.getStockQuantity(), p.getDescription()});
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + res.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
