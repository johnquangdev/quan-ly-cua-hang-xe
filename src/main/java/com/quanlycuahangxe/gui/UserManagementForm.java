package com.quanlycuahangxe.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.quanlycuahangxe.model.User;
import com.quanlycuahangxe.service.impl.UserServiceImpl;
import com.quanlycuahangxe.service.interfaces.UserService;
import com.quanlycuahangxe.utils.ServiceResult;

public class UserManagementForm extends JFrame {

    private JTextField txtUsername, txtFullName, txtEmail;
    private JPasswordField txtPassword;
    private JComboBox<String> cbRole;
    private JButton btnSave;
    private UserService userService = new UserServiceImpl();
    private User staff;
    private UserManagementPanel parentPanel;

    private static final String[] ROLES = {"ADMIN", "MANAGER", "STAFF"};

    public UserManagementForm(User staff, UserManagementPanel parentPanel) {
        this.staff = staff;
        this.parentPanel = parentPanel;
        setTitle(staff == null ? "Thêm nhân sự" : "Sửa nhân sự");
        setSize(450, 350);
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        mainPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        txtUsername = new JTextField(20);
        mainPanel.add(txtUsername, gbc);

        // Password field (hidden in edit mode)
        JPanel passwordPanel = new JPanel(new GridBagLayout());
        GridBagConstraints passwordGbc = new GridBagConstraints();
        passwordGbc.insets = new Insets(0, 0, 0, 0); 
        passwordGbc.anchor = GridBagConstraints.WEST;
        passwordGbc.fill = GridBagConstraints.HORIZONTAL;
        passwordGbc.gridx = 0;
        passwordGbc.gridy = 0;

        passwordPanel.add(new JLabel("Password:"), passwordGbc);
        passwordGbc.gridx = 1;
        txtPassword = new JPasswordField(20);
        passwordPanel.add(txtPassword, passwordGbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        mainPanel.add(passwordPanel, gbc);
        gbc.gridwidth = 1; 

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Full Name:"), gbc);
        gbc.gridx = 1;
        txtFullName = new JTextField(20);
        mainPanel.add(txtFullName, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = new JTextField(20);
        mainPanel.add(txtEmail, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        mainPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        // Lấy role người tạo
        String currentUserRole = parentPanel.getCurrentUser().getRole().toUpperCase();

        // Tạo danh sách role phù hợp
        DefaultComboBoxModel<String> roleModel = new DefaultComboBoxModel<>();
        if (currentUserRole.equals("ADMIN")) {
            roleModel.addElement("ADMIN");
            roleModel.addElement("MANAGER");
            roleModel.addElement("STAFF");
        } else if (currentUserRole.equals("MANAGER")) {
            roleModel.addElement("STAFF");
        }

        cbRole = new JComboBox<>(roleModel);

        mainPanel.add(cbRole, gbc);

        // Nếu sửa user
        if (staff != null) {
            txtUsername.setText(staff.getUsername());
            txtUsername.setEnabled(false);
            passwordPanel.setVisible(false); // Không cho đổi password 
            txtFullName.setText(staff.getFullName());
            txtEmail.setText(staff.getEmail());
            cbRole.setSelectedItem(staff.getRole());
            cbRole.setEnabled(false); // Không cho đổi role khi edit
        }

        // Nút lưu
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        btnSave = new JButton("Lưu");
        mainPanel.add(btnSave, gbc);

        btnSave.addActionListener(e -> saveStaff());

        add(mainPanel);
    }

    private void saveStaff() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String role = cbRole.getSelectedItem().toString().toUpperCase();

        // Validate email
        if (!email.endsWith("@gmail.com") || !Pattern.matches("^[\\w.+\\-]+@gmail\\.com$", email)) {
            showError("Email phải đúng định dạng @gmail.com");
            return;
        }

        // Validate password (chỉ khi tạo mới)
        if (staff == null && !isPasswordStrong(password)) {
            showError(
                    "Mật khẩu phải có ít nhất 8 ký tự, gồm chữ hoa, chữ thường, số và ký tự đặc biệt");
            return;
        }

        // Validate role
        if (!isValidRole(role)) {
            showError("Role không hợp lệ");
            return;
        }

        // Quyền tạo user
        String currentUserRole = parentPanel.getCurrentUser().getRole().toUpperCase();
        if (!canCreateRole(currentUserRole, role)) {
            showError("Bạn không có quyền tạo user với role này");
            return;
        }

        // Validate full name trùng lặp
        List<User> allUsers = userService.getAllUsers().getData();
        boolean duplicateName =
                allUsers.stream().anyMatch(u -> u.getFullName().equalsIgnoreCase(fullName)
                        && (staff == null || u.getId() != staff.getId()));
        if (duplicateName) {
            showError("Tên người dùng đã tồn tại");
            return;
        }

        ServiceResult<User> result;
        if (staff == null) {
            result = userService.createUser(null, username, password, fullName, email, role, "",
                    "");
        } else {
            result = userService.updateUser(staff.getId(), fullName, email, role);
        }

        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this, result.getMessage());
            parentPanel.loadStaffData();
            dispose();
        } else {
            showError(result.getMessage());
        }
    }

    private boolean isValidRole(String role) {
        for (String r : ROLES) {
            if (r.equals(role)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPasswordStrong(String password) {
        return password.length() >= 8 && password.matches(".*[A-Z].*")
                && password.matches(".*[a-z].*") && password.matches(".*\\d.*")
                && password.matches(".*[^a-zA-Z0-9].*");
    }

    private boolean canCreateRole(String currentRole, String targetRole) {
        switch (currentRole) {
            case "ADMIN":
                return true;
            case "MANAGER":
                return targetRole.equals("STAFF");
            default:
                return false;
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }
}
