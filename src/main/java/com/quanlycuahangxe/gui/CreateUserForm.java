package com.quanlycuahangxe.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

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

public class CreateUserForm extends JFrame {

    private JTextField txtUsername, txtFullName, txtEmail;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRole;
    private JButton btnCreate;

    private final UserService userService = new UserServiceImpl();
    private final User currentUser;

    public CreateUserForm(User currentUser) {
        this.currentUser = currentUser;

        setTitle("Tạo mới User");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;

        // Tài khoản
        panel.add(new JLabel("Tài khoản:"), getGbc(0, y));
        txtUsername = new JTextField(20);
        panel.add(txtUsername, getGbc(1, y++));

        // Mật khẩu
        panel.add(new JLabel("Mật khẩu:"), getGbc(0, y));
        txtPassword = new JPasswordField(20);
        panel.add(txtPassword, getGbc(1, y++));

        // Họ và tên
        panel.add(new JLabel("Họ và tên:"), getGbc(0, y));
        txtFullName = new JTextField(20);
        panel.add(txtFullName, getGbc(1, y++));

        // Email
        panel.add(new JLabel("Email:"), getGbc(0, y));
        txtEmail = new JTextField(20);
        panel.add(txtEmail, getGbc(1, y++));

        // Chức vụ
        panel.add(new JLabel("Chức vụ:"), getGbc(0, y));
        cmbRole = new JComboBox<>(new String[]{"MANAGER", "STAFF"});
        panel.add(cmbRole, getGbc(1, y++));

        // Nút tạo User
        btnCreate = new JButton("Tạo User");
        gbc.gridx = 1;
        gbc.gridy = y;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnCreate, gbc);

        btnCreate.addActionListener(e -> handleCreateUser());

        add(panel, BorderLayout.CENTER);
    }

    private GridBagConstraints getGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private void handleCreateUser() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String fullName = txtFullName.getText().trim();
        String email = txtEmail.getText().trim();
        String role = (String) cmbRole.getSelectedItem();

        ServiceResult<User> result = userService.createUser(currentUser, username, password, fullName, email, role, "", "");

        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // main test
//    public static void main(String[] args) {
//        User manager = new User();
//        manager.setRole("ADMIN"); // test role
//        SwingUtilities.invokeLater(() -> new CreateUserForm(manager).setVisible(true));
//    }
}
