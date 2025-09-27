package com.quanlycuahangxe.gui;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets; // Import IconHelper

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.quanlycuahangxe.model.User;
import com.quanlycuahangxe.service.impl.UserServiceImpl;
import com.quanlycuahangxe.service.interfaces.UserService;
import com.quanlycuahangxe.utils.IconHelper;
import com.quanlycuahangxe.utils.ServiceResult;

public class LoginForm extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private ImageIcon logoIcon; 

    private final UserService userService = new UserServiceImpl();

    public LoginForm() {
        setTitle("Đăng nhập hệ thống");
        setSize(450, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add logo
        logoIcon = IconHelper.loadIcon("logo.png", 60, 60); // Initialize logoIcon
        JLabel logoLabel = new JLabel(logoIcon);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across two columns
        gbc.anchor = GridBagConstraints.CENTER; // Center the logo
        panel.add(logoLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblUsername = new JLabel("Tài khoản:");
        gbc.gridx = 0;
        gbc.gridy = 1; 
        panel.add(lblUsername, gbc);

        txtUsername = new JTextField(20);
        gbc.gridx = 1;
        gbc.gridy = 1; 
        panel.add(txtUsername, gbc);

        JLabel lblPassword = new JLabel("Mật khẩu:");
        gbc.gridx = 0;
        gbc.gridy = 2; 
        panel.add(lblPassword, gbc);

        txtPassword = new JPasswordField(20);
        gbc.gridx = 1;
        gbc.gridy = 2; 
        panel.add(txtPassword, gbc);

        btnLogin = new JButton("Đăng nhập");
        gbc.gridx = 1;
        gbc.gridy = 3; 
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(btnLogin, gbc);

        btnLogin.addActionListener(e -> handleLogin());

        add(panel, BorderLayout.CENTER);
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        ServiceResult<User> result = userService.authenticateUser(username, password);

        if (result.isSuccess()) {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE, logoIcon);

            User currentUser = result.getData();

            // Mở MainForm và truyền currentUser vào
            SwingUtilities.invokeLater(() -> {
                new MainForm(currentUser).setVisible(true);
            });

            dispose();
        } else {
            JOptionPane.showMessageDialog(this, result.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE, logoIcon);
        }
    }

//    // Test nhanh
//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(() -> {
//            new LoginForm().setVisible(true);
//        });
//    }
}
