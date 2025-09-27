package com.quanlycuahangxe.gui;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.quanlycuahangxe.model.Customer;
import com.quanlycuahangxe.service.impl.CustomerServiceImpl;
import com.quanlycuahangxe.service.interfaces.CustomerService;
import com.quanlycuahangxe.utils.ServiceResult;

public class CustomerForm extends JDialog {

    private JTextField txtFullName, txtEmail, txtPhone, txtAddress;
    private JButton btnSave, btnCancel;
    private final CustomerService customerService = new CustomerServiceImpl();

    private Customer customer;
    private Window parentWindow;
    private CustomerManagementPanel parentPanel;

    public CustomerForm(Window owner, String title, Customer customer,
            CustomerManagementPanel parentPanel) {
        super(owner, title, Dialog.ModalityType.APPLICATION_MODAL);
        this.customer = customer;
        this.parentWindow = owner;
        this.parentPanel = parentPanel;
        setSize(400, 300);
        setLocationRelativeTo(owner);
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10)); // Sử dụng BorderLayout cho dialog chính

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 5, 5)); // Grid cho các trường nhập liệu
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Thêm padding

        formPanel.add(new JLabel("Họ tên:"));
        txtFullName = new JTextField(customer != null ? customer.getFullName() : "");
        formPanel.add(txtFullName);

        formPanel.add(new JLabel("Email:"));
        txtEmail = new JTextField(customer != null ? customer.getEmail() : "");
        formPanel.add(txtEmail);

        formPanel.add(new JLabel("Số điện thoại:"));
        txtPhone = new JTextField(customer != null ? customer.getPhone() : "");
        formPanel.add(txtPhone);

        formPanel.add(new JLabel("Địa chỉ:"));
        txtAddress = new JTextField(customer != null ? customer.getAddress() : "");
        formPanel.add(txtAddress);

        add(formPanel, BorderLayout.CENTER); // Thêm panel form vào giữa

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT)); // FlowLayout cho các nút
        btnSave = new JButton("Lưu");
        btnSave.setIcon(com.quanlycuahangxe.utils.IconHelper.loadIcon("save.png")); // Thêm icon lưu
        btnCancel = new JButton("Hủy");
        btnCancel.setIcon(com.quanlycuahangxe.utils.IconHelper.loadIcon("cancel.png")); // Thêm icon hủy

        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        add(buttonPanel, BorderLayout.SOUTH); // Thêm panel nút vào phía nam

        btnSave.addActionListener(e -> handleSave());
        btnCancel.addActionListener(e -> dispose());
    }

    private void handleSave() {
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String address = txtAddress.getText().trim();

        if (fullName.isEmpty() || phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Họ tên và Số điện thoại là bắt buộc!");
            return;
        }

        ServiceResult<Customer> result;

        if (customer == null) {
            result = customerService.createCustomer(fullName, email, phone, address);
        } else {
            result = customerService.updateCustomer(customer.getId(), fullName, email, phone,
                    address);
        }

        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this, customer == null ? "Thêm khách hàng thành công!"
                    : "Cập nhật khách hàng thành công!");

            // Cập nhật lại dữ liệu trên CustomerManagementPanel nếu là panel cha
            if (parentPanel != null) {
                parentPanel.refreshCustomers();
            } else if (parentWindow instanceof CustomerManagementForm) {
                ((CustomerManagementForm) parentWindow).refreshTable();
            }

            // Xử lý trường hợp CustomerManagementForm và CustomerManagementPanel
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi: " + result.getMessage());
        }
    }
}
