package com.quanlycuahangxe.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.quanlycuahangxe.model.Customer;
import com.quanlycuahangxe.model.Invoice;
import com.quanlycuahangxe.model.InvoiceItem;
import com.quanlycuahangxe.model.Product;
import com.quanlycuahangxe.model.User;
import com.quanlycuahangxe.service.impl.CustomerServiceImpl;
import com.quanlycuahangxe.service.impl.InvoiceServiceImpl;
import com.quanlycuahangxe.service.impl.ProductServiceImpl;
import com.quanlycuahangxe.service.impl.UserServiceImpl;
import com.quanlycuahangxe.service.interfaces.CustomerService;
import com.quanlycuahangxe.service.interfaces.InvoiceService;
import com.quanlycuahangxe.service.interfaces.ProductService;
import com.quanlycuahangxe.service.interfaces.UserService;
import com.quanlycuahangxe.utils.CurrencyHelper;
import com.quanlycuahangxe.utils.ServiceResult;

public class InvoiceFormDialog extends JDialog {

    private User currentUser;
    private JComboBox<User> comboStaff;
    private JComboBox<Customer> comboCustomer;
    private JLabel lblCustomerPhone;
    private JLabel lblCustomerAddress;
    private JComboBox<Product> comboProduct;
    private JTextField txtQuantity;
    private JLabel lblStock;
    private JButton btnAddItem, btnRemoveItem, btnSaveInvoice;
    private JTable tableItems;
    private DefaultTableModel tableModel;
    private JLabel lblTotal;

    private final InvoiceService invoiceService = new InvoiceServiceImpl();
    private final ProductService productService = new ProductServiceImpl();
    private final CustomerService customerService = new CustomerServiceImpl();
    private final UserService userService = new UserServiceImpl();

    private boolean saved = false;
    private Integer invoiceId = null;

    // Constructors
    public InvoiceFormDialog(User currentUser) {
        super((Frame) null, "Tạo hợp đồng mới", true);
        this.currentUser = currentUser;
        initUI();
    }

    public InvoiceFormDialog(User currentUser, int invoiceId) {
        super((Frame) null, "Sửa hợp đồng #" + invoiceId, true);
        this.currentUser = currentUser;
        this.invoiceId = invoiceId;
        initUI();
        loadInvoice(invoiceId);
    }

    public InvoiceFormDialog(Frame parent, User currentUser) {
        super(parent, "Tạo hợp đồng mới", true);
        this.currentUser = currentUser;
        initUI();
    }

    public InvoiceFormDialog(Frame parent, User currentUser, int invoiceId) {
        super(parent, "Sửa hợp đồng #" + invoiceId, true);
        this.currentUser = currentUser;
        this.invoiceId = invoiceId;
        initUI();
        loadInvoice(invoiceId);
    }

    public InvoiceFormDialog(Component parentComponent, User currentUser, int invoiceId) {
        super(SwingUtilities.getWindowAncestor(parentComponent), "Sửa hợp đồng #" + invoiceId,
                Dialog.ModalityType.APPLICATION_MODAL);
        this.currentUser = currentUser;
        this.invoiceId = invoiceId;
        initUI();
        loadInvoice(invoiceId);
    }

    // Khởi tạo giao diện người dùng
    private void initUI() {
        setSize(700, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Panel thông tin hợp đồng
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        comboStaff = new JComboBox<>();
        comboCustomer = new JComboBox<>();
        comboProduct = new JComboBox<>();
        txtQuantity = new JTextField(5);
        lblStock = new JLabel("Tồn kho: ");
        btnAddItem = new JButton("Thêm sản phẩm");

        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("Nhân viên:"), gbc);
        gbc.gridx = 1;
        topPanel.add(comboStaff, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        topPanel.add(new JLabel("Khách hàng:"), gbc);
        gbc.gridx = 1;
        topPanel.add(comboCustomer, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        topPanel.add(new JLabel("SĐT khách hàng:"), gbc);
        gbc.gridx = 1;
        lblCustomerPhone = new JLabel();
        topPanel.add(lblCustomerPhone, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        topPanel.add(new JLabel("Địa chỉ khách hàng:"), gbc);
        gbc.gridx = 1;
        lblCustomerAddress = new JLabel();
        topPanel.add(lblCustomerAddress, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        topPanel.add(new JLabel("Sản phẩm:"), gbc);
        gbc.gridx = 1;
        topPanel.add(comboProduct, gbc);
        gbc.gridx = 2;
        topPanel.add(lblStock, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        topPanel.add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 1;
        topPanel.add(txtQuantity, gbc);
        gbc.gridx = 2;
        gbc.gridwidth = 2;
        topPanel.add(btnAddItem, gbc);

        add(topPanel, BorderLayout.NORTH);

        // Bảng chi tiết sản phẩm trong hợp đồng
        tableModel = new DefaultTableModel(
                new Object[]{"Sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableItems = new JTable(tableModel);

        // Renderer tùy chỉnh để hiển thị tên sản phẩm
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

        // Renderer tiền tệ cho cột đơn giá và thành tiền
        CurrencyRenderer currencyRenderer = new CurrencyRenderer();
        tableItems.getColumnModel().getColumn(2).setCellRenderer(currencyRenderer);
        tableItems.getColumnModel().getColumn(3).setCellRenderer(currencyRenderer);

        add(new JScrollPane(tableItems), BorderLayout.CENTER);

        // Panel tổng tiền và các nút hành động
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Tổng: 0.0");
        btnRemoveItem = new JButton("Xóa sản phẩm");
        btnSaveInvoice = new JButton("Lưu hợp đồng");

        bottomPanel.add(btnRemoveItem);
        bottomPanel.add(lblTotal);
        bottomPanel.add(btnSaveInvoice);

        add(bottomPanel, BorderLayout.SOUTH);

        loadStaffCustomerProduct();

        // Action listeners
        comboCustomer.addActionListener(e -> updateCustomerDetails());
        comboProduct.addActionListener(e -> updateStockLabel());
        btnAddItem.addActionListener(e -> addItem());
        btnRemoveItem.addActionListener(e -> removeItem());
        btnSaveInvoice.addActionListener(e -> saveInvoice());

        updateStockLabel();
        updateCustomerDetails();
    }

    // Tải dữ liệu cho ComboBox nhân viên, khách hàng và sản phẩm
    private void loadStaffCustomerProduct() {
        comboStaff.removeAllItems();
        ServiceResult<java.util.List<User>> resStaff = userService.getAllUsers();
        if (resStaff.isSuccess()) {
            for (User u : resStaff.getData()) {
                comboStaff.addItem(u);
            }
        }
        // Renderer tùy chỉnh để hiển thị tên đầy đủ của nhân viên
        comboStaff.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (value != null) {
                return new JLabel(((User) value).getFullName());
            }
            return new JLabel();
        });

        if (currentUser != null) {
            comboStaff.setSelectedItem(currentUser);
            if ("STAFF".equalsIgnoreCase(currentUser.getRole())) {
                comboStaff.setEnabled(false); 
            } else {
                comboStaff.setEnabled(true);
            }
        }

        // Tải danh sách khách hàng
        comboCustomer.removeAllItems();
        ServiceResult<List<Customer>> resCust = customerService.getAllCustomers();
        if (resCust.isSuccess()) {
            for (Customer c : resCust.getData()) {
                comboCustomer.addItem(c);
            }
        }
        // Renderer tùy chỉnh để hiển thị thông tin khách hàng
        comboCustomer.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (value != null) {
                Customer c = (Customer) value;
                return new JLabel(c.getFullName() + " - " + c.getPhone() + " - " + c.getEmail());
            }
            return new JLabel();
        });

        // Tải danh sách sản phẩm
        comboProduct.removeAllItems();
        ServiceResult<List<Product>> resProd = productService.getAllProducts();
        if (resProd.isSuccess()) {
            for (Product p : resProd.getData()) {
                comboProduct.addItem(p);
            }
        }
        // Renderer tùy chỉnh để hiển thị tên sản phẩm
        comboProduct.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (value != null) {
                return new JLabel(((Product) value).getName());
            }
            return new JLabel();
        });
    }

    // Quản lý các mặt hàng trong hợp đồng
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

        if (qty > prod.getStockQuantity()) {
            JOptionPane.showMessageDialog(this,
                    "Số lượng vượt quá tồn kho! Chỉ còn " + prod.getStockQuantity() + " sản phẩm.");
            return;
        }

        double price = prod.getPrice();
        double total = price * qty;
        tableModel.addRow(new Object[]{prod, qty, price, total});
        updateTotal();
    }

    private void removeItem() {
        int row = tableItems.getSelectedRow();
        if (row >= 0) {
            tableModel.removeRow(row);
            updateTotal();
        }
    }

    private void updateTotal() {
        double sum = 0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            Object v = tableModel.getValueAt(i, 3);
            if (v instanceof Number) {
                sum += ((Number) v).doubleValue();
            } else {
                try {
                    sum += Double.parseDouble(String.valueOf(v));
                } catch (Exception ignored) {
                }
            }
        }
        lblTotal.setText("Tổng: " + CurrencyHelper.formatVND(sum));
    }

    private void updateStockLabel() {
        Product selectedProduct = (Product) comboProduct.getSelectedItem();
        if (selectedProduct != null) {
            lblStock.setText("Tồn kho: " + selectedProduct.getStockQuantity());
        } else {
            lblStock.setText("Tồn kho: ");
        }
    }

    // Các thao tác với hóa đơn
    private void saveInvoice() {
        User staff = (User) comboStaff.getSelectedItem();
        Customer customer = (Customer) comboCustomer.getSelectedItem();
        if (staff == null || customer == null) {
            JOptionPane.showMessageDialog(this, "Chọn nhân viên và khách hàng");
            return;
        }

        if (invoiceId == null) {
            // Tạo mới hợp đồng
            ServiceResult<Invoice> res
                    = invoiceService.createInvoice(customer.getId(), staff.getId());
            if (!res.isSuccess()) {
                JOptionPane.showMessageDialog(this, res.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            Invoice inv = res.getData();
            // Thêm các sản phẩm vào hợp đồng
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Product p = (Product) tableModel.getValueAt(i, 0);
                int qty = (int) tableModel.getValueAt(i, 1);
                double price = ((Number) tableModel.getValueAt(i, 2)).doubleValue();
                invoiceService.addInvoiceItem(inv.getId(), p.getId(), qty, price);
            }
            invoiceService.calculateTotal(inv.getId());
            JOptionPane.showMessageDialog(this, "Lưu hợp đồng thành công");

        } else {
            // Cập nhật hợp đồng hiện có
            List<InvoiceItem> newItems = new java.util.ArrayList<>();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Product p = (Product) tableModel.getValueAt(i, 0);
                int qty = (int) tableModel.getValueAt(i, 1);
                double price = ((Number) tableModel.getValueAt(i, 2)).doubleValue();
                newItems.add(new InvoiceItem(0, invoiceId, p.getId(), qty, price));
            }

            ServiceResult<Invoice> updateRes = invoiceService.updateInvoice(invoiceId,
                    customer.getId(), staff.getId(), newItems);

            if (updateRes.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Cập nhật hợp đồng thành công");
            } else {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi cập nhật hợp đồng: " + updateRes.getMessage(), "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        saved = true;
        dispose();
    }

    private void loadInvoice(int invoiceId) {
        ServiceResult<Invoice> res = invoiceService.getInvoiceDetails(invoiceId);
        if (!res.isSuccess()) {
            JOptionPane.showMessageDialog(this,
                    "Không thể tải chi tiết hóa đơn: " + res.getMessage());
            return;
        }
        Invoice inv = res.getData();

        for (int i = 0; i < comboCustomer.getItemCount(); i++) {
            Customer c = comboCustomer.getItemAt(i);
            if (c != null && c.getId() == inv.getCustomerId()) {
                comboCustomer.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 0; i < comboStaff.getItemCount(); i++) {
            User u = comboStaff.getItemAt(i);
            if (u != null && u.getId() == inv.getUserId()) {
                comboStaff.setSelectedIndex(i);
                break;
            }
        }

        lblTotal.setText("Tổng: " + CurrencyHelper.formatVND(inv.getTotalAmount()));

        tableModel.setRowCount(0);
        if (inv.getItems() != null) {
            for (InvoiceItem item : inv.getItems()) {
                Product p = new Product();
                p.setId(item.getProductId());
                p.setName(item.getProductName());
                p.setPrice(item.getPrice());

                tableModel.addRow(new Object[]{p, item.getQuantity(), item.getPrice(),
                    item.getQuantity() * item.getPrice()});
            }
        }
    }

    private void updateCustomerDetails() {
        Customer selectedCustomer = (Customer) comboCustomer.getSelectedItem();
        if (selectedCustomer != null) {
            lblCustomerPhone.setText(selectedCustomer.getPhone());
            lblCustomerAddress.setText(selectedCustomer.getAddress());
        } else {
            lblCustomerPhone.setText("");
            lblCustomerAddress.setText("");
        }
    }

    public boolean isSaved() {
        return saved;
    }
}
