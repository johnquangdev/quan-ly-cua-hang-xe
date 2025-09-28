package com.quanlycuahangxe.gui;

import com.quanlycuahangxe.model.InventoryLog;
import com.quanlycuahangxe.model.Product;
import com.quanlycuahangxe.service.impl.InventoryLogServiceImpl;
import com.quanlycuahangxe.service.impl.ProductServiceImpl;
import com.quanlycuahangxe.service.interfaces.InventoryLogService;
import com.quanlycuahangxe.service.interfaces.ProductService;
import com.quanlycuahangxe.utils.IconHelper;
import com.quanlycuahangxe.utils.ServiceResult;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ImportStockDialog extends JDialog {

    private JComboBox<Product> comboProduct;
    private JTextField txtQuantity;
    private JTextField txtReason;
    private JButton btnAddItem, btnRemoveItem, btnSaveImport;
    private JTable tableItems;
    private DefaultTableModel tableModel;

    private final ProductService productService = new ProductServiceImpl();
    private final InventoryLogService inventoryLogService = new InventoryLogServiceImpl();

    private boolean saved = false;

    public ImportStockDialog(Frame owner) {
        super(owner, "Nhập kho sản phẩm", true);
        initUI();
    }

    private void initUI() {
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top panel: chọn sản phẩm, số lượng, lý do
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        comboProduct = new JComboBox<>();
        txtQuantity = new JTextField(5);
        txtReason = new JTextField("Nhập kho");
        btnAddItem = new JButton("Thêm vào phiếu");
        btnAddItem.setIcon(IconHelper.loadIcon("add.png"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("Sản phẩm:"), gbc);
        gbc.gridx = 1;
        topPanel.add(comboProduct, gbc);

        gbc.gridx = 2;
        topPanel.add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 3;
        topPanel.add(txtQuantity, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        topPanel.add(new JLabel("Lý do:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        topPanel.add(txtReason, gbc);
        gbc.gridx = 3;
        gbc.gridwidth = 1;
        topPanel.add(btnAddItem, gbc);

        add(topPanel, BorderLayout.NORTH);

        // Panel bảng hiển thị các mặt hàng đã thêm
        tableModel = new DefaultTableModel(new Object[]{"Sản phẩm", "Số lượng", "Lý do"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableItems = new JTable(tableModel);

        // Renderer cho cột sản phẩm để hiển thị tên sản phẩm
        tableItems.getColumnModel().getColumn(0).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            protected void setValue(Object value) {
                if (value instanceof Product) {
                    setText(((Product) value).getName());
                } else {
                    super.setValue(value);
                }
            }
        });

        add(new JScrollPane(tableItems), BorderLayout.CENTER);

        // Panel dưới cùng: nút xóa và lưu phiếu nhập
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRemoveItem = new JButton("Xóa khỏi phiếu");
        btnRemoveItem.setIcon(IconHelper.loadIcon("delete.png"));
        btnSaveImport = new JButton("Lưu phiếu nhập");
        btnSaveImport.setIcon(IconHelper.loadIcon("save.png"));

        bottomPanel.add(btnRemoveItem);
        bottomPanel.add(btnSaveImport);

        add(bottomPanel, BorderLayout.SOUTH);

        // Load data cho combo
        loadProducts();

        btnAddItem.addActionListener(e -> addItem());
        btnRemoveItem.addActionListener(e -> removeItem());
        btnSaveImport.addActionListener(e -> saveImport());
    }

    public ImportStockDialog(Window owner) {
        super(owner, "Nhập kho", ModalityType.APPLICATION_MODAL);
        initUI();
    }

    private void loadProducts() {
        comboProduct.removeAllItems();
        ServiceResult<List<Product>> resProd = productService.getAllProducts();
        if (resProd.isSuccess()) {
            for (Product p : resProd.getData()) {
                comboProduct.addItem(p);
            }
        }
        comboProduct.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (value != null) {
                return new JLabel(((Product) value).getName());
            }
            return new JLabel();
        });
    }

    private void addItem() {
        Product prod = (Product) comboProduct.getSelectedItem();
        if (prod == null) {
            return;
        }
        int qty;
        try {
            qty = Integer.parseInt(txtQuantity.getText().trim());
            if (qty <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Nhập số lượng hợp lệ!");
            return;
        }
        String reason = txtReason.getText().trim();
        if (reason.isEmpty()) {
            reason = "Nhập kho";
        }

        // Kiểm tra xem sản phẩm đã có trong bảng chưa, nếu có thì cộng dồn số lượng
        boolean found = false;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Product existingProd = (Product) tableModel.getValueAt(i, 0);
            if (existingProd.getId() == prod.getId()) {
                int currentQty = (int) tableModel.getValueAt(i, 1);
                tableModel.setValueAt(currentQty + qty, i, 1);
                found = true;
                break;
            }
        }

        if (!found) {
            tableModel.addRow(new Object[]{prod, qty, reason});
        }
        txtQuantity.setText("");
    }

    private void removeItem() {
        int row = tableItems.getSelectedRow();
        if (row >= 0) {
            tableModel.removeRow(row);
        }
    }

    private void saveImport() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng thêm sản phẩm vào phiếu nhập.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận lưu phiếu nhập kho?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            Connection conn = null;
            try {
                conn = ((ProductServiceImpl) productService).getDataSource().getConnection();
                conn.setAutoCommit(false);

                boolean allSuccess = true;
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    Product p = (Product) tableModel.getValueAt(i, 0);
                    int qty = (int) tableModel.getValueAt(i, 1);
                    String reason = (String) tableModel.getValueAt(i, 2);

                    // Cập nhật tồn kho sản phẩm
                    ServiceResult<Product> updateStockRes = productService.updateStock(conn, p.getId(), qty);
                    if (!updateStockRes.isSuccess()) {
                        allSuccess = false;
                        JOptionPane.showMessageDialog(this, "Lỗi cập nhật tồn kho cho sản phẩm " + p.getName() + ": " + updateStockRes.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        break;
                    }

                    // Ghi log nhập kho
                    ServiceResult<InventoryLog> addLogRes = inventoryLogService.addLog(conn, p.getId(), qty, reason);
                    if (!addLogRes.isSuccess()) {
                        allSuccess = false;
                        JOptionPane.showMessageDialog(this, "Lỗi ghi log cho sản phẩm " + p.getName() + ": " + addLogRes.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                        break;
                    }
                }

                if (allSuccess) {
                    conn.commit();
                    JOptionPane.showMessageDialog(this, "Phiếu nhập kho đã được lưu thành công!");
                    saved = true;
                    dispose();
                } else {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this, "Có lỗi xảy ra trong quá trình lưu phiếu nhập kho. Vui lòng kiểm tra log.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                if (conn != null) {
                    try {
                        conn.rollback();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
                JOptionPane.showMessageDialog(this, "Lỗi SQL khi lưu phiếu nhập kho: " + e.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
    }

    public boolean isSaved() {
        return saved;
    }
}
