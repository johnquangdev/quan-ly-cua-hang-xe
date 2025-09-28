package com.quanlycuahangxe.gui;

import com.quanlycuahangxe.model.User;
import com.quanlycuahangxe.service.impl.UserServiceImpl;
import com.quanlycuahangxe.service.interfaces.UserService;
import com.quanlycuahangxe.utils.IconHelper;
import com.quanlycuahangxe.utils.ServiceResult;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainForm extends JFrame {

    private JPanel sideMenu;
    private JPanel contentPanel;
    private User currentUser;
    private JButton btnUserInfo;

    public MainForm(User currentUser) {
        this.currentUser = currentUser;
        setTitle("Hệ thống quản lý - Xin chào " + currentUser.getFullName());
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set custom icon for JOptionPane
        ImageIcon customIcon = IconHelper.loadIcon("logo.png", 120, 120);
        if (customIcon != null) {
            UIManager.put("OptionPane.informationIcon", customIcon);
            UIManager.put("OptionPane.questionIcon", customIcon);
            UIManager.put("OptionPane.warningIcon", customIcon);
            UIManager.put("OptionPane.errorIcon", customIcon);
        }

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        //Thanh menu bên trái
        sideMenu = new JPanel();
        sideMenu.setLayout(new GridLayout(10, 1, 5, 5));
        sideMenu.setPreferredSize(new Dimension(200, 0));
        sideMenu.setBackground(new Color(240, 240, 240));

        JButton btnProduct = new JButton("Quản lý sản phẩm");
        btnProduct.setIcon(IconHelper.loadIcon("product.png", 20, 20));
        btnProduct.addActionListener(e -> {
            String role = currentUser.getRole().toUpperCase();
            if (role.equals("STAFF")) {
                JOptionPane.showMessageDialog(this, "Bạn không có quyền xem quản lý sản phẩm",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            setContent(new ProductManagementPanel());
        });
        sideMenu.add(btnProduct);

        JButton btnStaff = new JButton("Quản lý nhân sự");
        btnStaff.setIcon(IconHelper.loadIcon("staff.png", 20, 20));
        btnStaff.addActionListener(e -> {
            // Chỉ admin hoặc manager mới xem được
            String role = currentUser.getRole().toUpperCase();
            if (role.equals("STAFF")) {
                JOptionPane.showMessageDialog(this, "Bạn không có quyền xem quản lý nhân sự",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Tạo panel với currentUser
            UserManagementPanel staffPanel = new UserManagementPanel(currentUser);
            setContent(staffPanel);
        });

        JButton btnCustomer = new JButton("Quản lý khách hàng");
        btnCustomer.setIcon(IconHelper.loadIcon("customer.png", 20, 20));
        btnCustomer.addActionListener(e -> {
            setContent(new CustomerManagementPanel());
        });

        JButton btnInvoice = new JButton("Quản lý hóa đơn");
        btnInvoice.setIcon(IconHelper.loadIcon("invoice.png", 20, 20));
        btnInvoice.addActionListener(e -> setContent(new InvoiceManagementPanel(currentUser)));
        sideMenu.add(btnInvoice);

        sideMenu.add(btnProduct);
        sideMenu.add(btnStaff);
        sideMenu.add(btnCustomer);

        // Nội dung chính 
        contentPanel = new JPanel(new BorderLayout());
        JLabel welcomeLabel
                = new JLabel("Chào mừng bạn đến hệ thống quản lý!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        contentPanel.add(welcomeLabel, BorderLayout.CENTER);

        // Nút user info (phía trên cùng) 
        String userText = currentUser.getFullName() + " (" + currentUser.getRole() + ")";
        btnUserInfo = new JButton(userText);

        // Popup menu
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem itemMoreInfo = new JMenuItem("Xem thêm thông tin");
        JMenuItem itemChangePassword = new JMenuItem("Đổi mật khẩu");
        JMenuItem itemLogout = new JMenuItem("Đăng xuất");

        // Xử lý "Xem thêm thông tin"
        itemMoreInfo.addActionListener((ActionEvent e) -> {
            JOptionPane.showMessageDialog(this,
                    "Tên: " + currentUser.getFullName() + "\nChức vụ: " + currentUser.getRole()
                    + "\nEmail: " + currentUser.getEmail(),
                    "Thông tin người dùng", JOptionPane.INFORMATION_MESSAGE);
        });

        // Xử lý "Đăng xuất"
        itemLogout.addActionListener((ActionEvent e) -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
            }
        });

        popupMenu.add(itemMoreInfo);
        popupMenu.add(itemChangePassword);
        popupMenu.add(itemLogout);

        btnUserInfo.addActionListener(e -> popupMenu.show(btnUserInfo, 0, btnUserInfo.getHeight()));

        // Xử lý "Đổi mật khẩu"
        itemChangePassword.addActionListener((ActionEvent e) -> {
            handleChangePassword();
        });

        // Thêm vào giao diện
        add(btnUserInfo, BorderLayout.NORTH);
        add(sideMenu, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Nút thoát chương trình 
        JButton btnExit = new JButton("Thoát chương trình");
        btnExit.setIcon(IconHelper.loadIcon("exit.png", 20, 20));
        btnExit.addActionListener(e -> {
            int confirm
                    = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn thoát chương trình?",
                            "Xác nhận thoát", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });

        // Thêm nút vào cuối menu bên trái
        sideMenu.add(btnExit);

    }

    private void setContent(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void handleChangePassword() {
        ChangePasswordDialog dialog = new ChangePasswordDialog(this, currentUser);
        dialog.setVisible(true);
    }

    // Custom JDialog for changing password
    private class ChangePasswordDialog extends JDialog {

        private JPasswordField oldPasswordField;
        private JPasswordField newPasswordField;
        private JPasswordField confirmPasswordField;
        private JLabel errorLabel;
        private User user;
        private UserService userService;

        public ChangePasswordDialog(Frame owner, User user) {
            super(owner, "Đổi mật khẩu", true);
            this.user = user;
            this.userService = new UserServiceImpl();

            initDialogUI();
            setSize(400, 300);
            setLocationRelativeTo(owner);
        }

        private void initDialogUI() {
            setLayout(new BorderLayout(10, 10));
            JPanel formPanel = new JPanel(new GridLayout(0, 1, 5, 5));
            formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            oldPasswordField = new JPasswordField(20);
            newPasswordField = new JPasswordField(20);
            confirmPasswordField = new JPasswordField(20);
            errorLabel = new JLabel("");
            errorLabel.setForeground(Color.RED);

            formPanel.add(new JLabel("Mật khẩu hiện tại:"));
            formPanel.add(oldPasswordField);
            formPanel.add(new JLabel("Mật khẩu mới:"));
            formPanel.add(newPasswordField);
            formPanel.add(new JLabel("Xác nhận mật khẩu mới:"));
            formPanel.add(confirmPasswordField);
            formPanel.add(errorLabel);
            formPanel.add(new JLabel(
                    "<html><font color='red'>Lưu ý: Nếu quên mật khẩu hiện tại, vui lòng liên hệ ADMIN.</font></html>"));

            add(formPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton btnChange = new JButton("Đổi mật khẩu");
            JButton btnCancel = new JButton("Hủy");

            btnChange.addActionListener(e -> handleChangePasswordAction());
            btnCancel.addActionListener(e -> dispose());

            buttonPanel.add(btnChange);
            buttonPanel.add(btnCancel);
            add(buttonPanel, BorderLayout.SOUTH);
        }

        private void handleChangePasswordAction() {
            String oldPassword = new String(oldPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            errorLabel.setText("");

            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                errorLabel.setText("Vui lòng điền đầy đủ các trường mật khẩu.");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                errorLabel.setText("Mật khẩu mới và xác nhận mật khẩu không khớp.");
                return;
            }

            ServiceResult<Void> changeResult
                    = userService.changePassword(user.getId(), oldPassword, newPassword);

            if (changeResult.isSuccess()) {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose(); // Close dialog on success
            } else {
                errorLabel.setText("Lỗi: " + changeResult.getMessage());
            }
        }
    }
}
