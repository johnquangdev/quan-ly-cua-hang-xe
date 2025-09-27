package com.quanlycuahangxe.gui;

import java.awt.BorderLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.quanlycuahangxe.model.Customer;
import com.quanlycuahangxe.service.impl.CustomerServiceImpl;
import com.quanlycuahangxe.service.interfaces.CustomerService;
import com.quanlycuahangxe.utils.ServiceResult;

public class CustomerManagementForm extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh;

    private final CustomerService customerService = new CustomerServiceImpl();

    public CustomerManagementForm() {
        setTitle("Quản lý Khách hàng");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        loadCustomers();
    }

    private void initUI() {
        // Panel chính của form
        setLayout(new BorderLayout(10, 10));

        // Bảng hiển thị danh sách khách hàng
        String[] columns = {"ID", "Họ tên", "Email", "Số điện thoại", "Địa chỉ", "Ngày tạo"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Panel chứa các nút chức năng
        JPanel panelButtons = new JPanel();
        btnAdd = new JButton("Thêm");
        btnEdit = new JButton("Sửa");
        btnDelete = new JButton("Xóa");
        btnRefresh = new JButton("Làm mới");

        panelButtons.add(btnAdd);
        panelButtons.add(btnEdit);
        panelButtons.add(btnDelete);
        panelButtons.add(btnRefresh);

        add(panelButtons, BorderLayout.SOUTH);

        // Gắn các sự kiện cho nút
        btnAdd.addActionListener(e -> openAddForm());
        btnEdit.addActionListener(e -> openEditForm());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnRefresh.addActionListener(e -> loadCustomers());
    }

    private void loadCustomers() {
        tableModel.setRowCount(0); // clear
        ServiceResult<List<Customer>> result = customerService.getAllCustomers();
        if (result.isSuccess()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            for (Customer c : result.getData()) {
                tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getFullName(),
                    c.getEmail(),
                    c.getPhone(),
                    c.getAddress(),
                    c.getCreatedAt() != null ? formatter.format(c.getCreatedAt()) : "n/a"
                });
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + result.getMessage());
        }
    }

    private void openAddForm() {
        CustomerForm form = new CustomerForm(this, "Thêm Khách hàng", null, null);
        form.setVisible(true);
    }

    private void openEditForm() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để sửa.");
            return;
        }
        int id = (int) tableModel.getValueAt(selected, 0);
        ServiceResult<Customer> result = customerService.getCustomerById(id);
        if (result.isSuccess()) {
            CustomerForm form = new CustomerForm(this, "Sửa Khách hàng", result.getData(), null);
            form.setVisible(true);
        }
    }

    private void deleteCustomer() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng để xóa.");
            return;
        }
        int id = (int) tableModel.getValueAt(selected, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa khách hàng này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            ServiceResult<Void> result = customerService.deleteCustomer(id);
            if (result.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Đã xóa thành công!");
                loadCustomers();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: " + result.getMessage());
            }
        }
    }

    public void refreshTable() {
        loadCustomers();
    }
    // Main test GUI
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            new CustomerManagementForm().setVisible(true);
//        });
//    }
}
