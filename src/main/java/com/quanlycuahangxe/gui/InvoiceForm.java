package com.quanlycuahangxe.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.quanlycuahangxe.model.Customer;
import com.quanlycuahangxe.model.Invoice;
import com.quanlycuahangxe.model.Product;
import com.quanlycuahangxe.service.impl.CustomerServiceImpl;
import com.quanlycuahangxe.service.impl.InvoiceServiceImpl;
import com.quanlycuahangxe.service.impl.ProductServiceImpl;
import com.quanlycuahangxe.service.interfaces.CustomerService;
import com.quanlycuahangxe.service.interfaces.InvoiceService;
import com.quanlycuahangxe.service.interfaces.ProductService;
import com.quanlycuahangxe.utils.ServiceResult;

public class InvoiceForm extends JPanel {

    private JComboBox<Customer> comboCustomer;
    private JComboBox<Product> comboProduct;
    private JTextField txtQuantity;
    private JButton btnAddItem, btnRemoveItem, btnSaveInvoice;
    private JTable tableItems;
    private DefaultTableModel tableModel;
    private JLabel lblTotal;

    private InvoiceService invoiceService = new InvoiceServiceImpl();
    private ProductService productService = new ProductServiceImpl();
    private CustomerService customerService = new CustomerServiceImpl();

    public InvoiceForm() {
        setLayout(new BorderLayout(10, 10));

        // Panel trên cùng: chọn khách hàng và sản phẩm
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        comboCustomer = new JComboBox<>();
        comboProduct = new JComboBox<>();
        txtQuantity = new JTextField(5);
        btnAddItem = new JButton("Xác nhận");

        gbc.gridx = 0;
        gbc.gridy = 0;
        topPanel.add(new JLabel("Khách hàng:"), gbc);
        gbc.gridx = 1;
        topPanel.add(comboCustomer, gbc);
        gbc.gridx = 0;
        gbc.gridy = 1;
        topPanel.add(new JLabel("Sản phẩm:"), gbc);
        gbc.gridx = 1;
        topPanel.add(comboProduct, gbc);
        gbc.gridx = 2;
        topPanel.add(new JLabel("Số lượng:"), gbc);
        gbc.gridx = 3;
        topPanel.add(txtQuantity, gbc);
        gbc.gridx = 4;
        topPanel.add(btnAddItem, gbc);

        add(topPanel, BorderLayout.NORTH);

        // Panel bảng hiển thị các mặt hàng trong hóa đơn
        tableModel = new DefaultTableModel(new Object[]{"Sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableItems = new JTable(tableModel);
        add(new JScrollPane(tableItems), BorderLayout.CENTER);

        // Panel dưới cùng: tổng tiền và nút lưu hóa đơn
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotal = new JLabel("Tổng: 0.0");
        btnRemoveItem = new JButton("Xóa sản phẩm");
        btnSaveInvoice = new JButton("Lưu hóa đơn");

        bottomPanel.add(btnRemoveItem);
        bottomPanel.add(lblTotal);
        bottomPanel.add(btnSaveInvoice);

        add(bottomPanel, BorderLayout.SOUTH);

        loadCustomersAndProducts();

        // Action listeners
        btnAddItem.addActionListener(e -> addItem());
        btnRemoveItem.addActionListener(e -> removeItem());
        btnSaveInvoice.addActionListener(e -> saveInvoice());
    }

    private void loadCustomersAndProducts() {
        comboCustomer.removeAllItems();
        ServiceResult<List<Customer>> resCust = customerService.getAllCustomers();
        if (resCust.isSuccess()) {
            for (Customer c : resCust.getData()) {
                comboCustomer.addItem(c);
            }
        }

        comboProduct.removeAllItems();
        ServiceResult<List<Product>> resProd = productService.getAllProducts();
        if (resProd.isSuccess()) {
            for (Product p : resProd.getData()) {
                comboProduct.addItem(p);
            }
        }

        // Custom renderer để hiển thị tên khách hàng thay vì đối tượng Customer
        comboCustomer.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (value != null) {
                return new JLabel(((Customer) value).getFullName());
            }
            return new JLabel();
        });
        // Custom renderer để hiển thị tên sản phẩm thay vì đối tượng Product
        comboProduct.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            if (value != null) {
                return new JLabel(((Product) value).getName());
            }
            return new JLabel();
        });
    }

    private void addItem() {
        Product prod = (Product) comboProduct.getSelectedItem();
        int qty = Integer.parseInt(txtQuantity.getText().trim());
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
            sum += (double) tableModel.getValueAt(i, 3);
        }
        lblTotal.setText("Tổng: " + sum);
    }

    private void saveInvoice() {
        Customer customer = (Customer) comboCustomer.getSelectedItem();
        // Giả sử nhân viên hiện tại là staffId = 1 (cần thay đổi để lấy từ session)
        int staffId = 1;

        ServiceResult<Invoice> res = invoiceService.createInvoice(customer.getId(), staffId);
        if (res.isSuccess()) {
            Invoice inv = res.getData();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                Product p = (Product) tableModel.getValueAt(i, 0);
                int qty = (int) tableModel.getValueAt(i, 1);
                double price = (double) tableModel.getValueAt(i, 2);
                invoiceService.addInvoiceItem(inv.getId(), p.getId(), qty, price);
            }
            invoiceService.calculateTotal(inv.getId());
            JOptionPane.showMessageDialog(this, "Lưu hóa đơn thành công");
            tableModel.setRowCount(0);
            updateTotal();
        } else {
            JOptionPane.showMessageDialog(this, res.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
