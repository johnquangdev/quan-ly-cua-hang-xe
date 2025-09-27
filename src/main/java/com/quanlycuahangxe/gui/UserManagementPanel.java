package com.quanlycuahangxe.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.quanlycuahangxe.model.User;
import com.quanlycuahangxe.service.impl.UserServiceImpl;
import com.quanlycuahangxe.service.interfaces.UserService;
import com.quanlycuahangxe.utils.ServiceResult;

public class UserManagementPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete, btnToggleLock;
    private JTextField txtSearch; 
    private JButton btnSearch; 
    private JButton btnRefresh;

    private UserService userService = new UserServiceImpl();
    private User currentUser;

    public UserManagementPanel(User currentUser) {
        this.currentUser = currentUser;

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
                new Object[] {"ID", "Username", "Full Name", "Email", "Role", "Locked"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        CustomTableCellRenderer renderer = new CustomTableCellRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        //  Buttons 
        JPanel buttonPanel = new JPanel(new GridLayout(1, 0, 10, 0));
        btnAdd = new JButton("Thêm nhân sự");
        btnAdd.setIcon(com.quanlycuahangxe.utils.IconHelper.loadIcon("add.png"));
        btnEdit = new JButton("Sửa nhân sự");
        btnEdit.setIcon(com.quanlycuahangxe.utils.IconHelper.loadIcon("edit.png"));
        btnDelete = new JButton("Xóa nhân sự");
        btnDelete.setIcon(com.quanlycuahangxe.utils.IconHelper.loadIcon("delete.png"));
        btnToggleLock = new JButton("Khóa/Mở khóa");
        btnToggleLock.setIcon(com.quanlycuahangxe.utils.IconHelper.loadIcon("lock.png"));

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnToggleLock);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load data 
        loadStaffData();

        // Action listeners 
        btnAdd.addActionListener(e -> {
            UserManagementForm form = new UserManagementForm(null, this);
            form.setVisible(true);
        });

        btnEdit.addActionListener(e -> editSelectedUser());

        btnDelete.addActionListener(e -> deleteSelectedUser());

        btnToggleLock.addActionListener(e -> toggleLockSelectedUser());

        btnSearch.addActionListener(e -> loadStaffData(txtSearch.getText()));
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadStaffData(null);
        });
    }

    UserManagementPanel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public User getCurrentUser() {
        return currentUser;
    }

    private void editSelectedUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn nhân viên để sửa", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        int userId = (int) tableModel.getValueAt(row, 0);
        ServiceResult<User> result = userService.getUserById(userId);
        if (result.isSuccess()) {
            UserManagementForm form = new UserManagementForm(result.getData(), this);
            form.setVisible(true);
        }
    }

    private void deleteSelectedUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn nhân viên để xóa", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        User targetUser = getUserFromRow(row);
        if (!canDelete(currentUser, targetUser)) {
            JOptionPane.showMessageDialog(this, "Bạn không có quyền xóa user này", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn chắc chắn muốn xóa nhân viên này?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            ServiceResult<Void> del = userService.deleteUser(targetUser.getId());
            JOptionPane.showMessageDialog(this, del.getMessage());
            loadStaffData();
        }
    }

    private void toggleLockSelectedUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Chọn nhân viên để khóa/mở khóa", "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        User targetUser = getUserFromRow(row);
        if (currentUser.getId() == targetUser.getId()) {
            JOptionPane.showMessageDialog(this, "Bạn không thể khóa chính mình", "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        ServiceResult<Void> res = userService.toggleUserLock(targetUser.getId());
        JOptionPane.showMessageDialog(this, res.getMessage());
        loadStaffData();
    }

    private boolean canDelete(User currentUser, User targetUser) {
        String currRole = currentUser.getRole().toUpperCase();
        String targetRole = targetUser.getRole().toUpperCase();

        switch (currRole) {
            case "ADMIN":
                return !targetRole.equals("ADMIN");
            case "MANAGER":
                return targetRole.equals("STAFF");
            default:
                return false;
        }
    }

    private User getUserFromRow(int row) {
        int id = (int) tableModel.getValueAt(row, 0);
        ServiceResult<User> result = userService.getUserById(id);
        return result.isSuccess() ? result.getData() : null;
    }

    public void loadStaffData() {
        loadStaffData(null); // Default to loading all data
    }

    public void loadStaffData(String keyword) {
        tableModel.setRowCount(0);
        ServiceResult<List<User>> res;

        if (keyword == null || keyword.trim().isEmpty()) {
            res = userService.getAllUsers();
        } else {
            res = userService.searchUsers(keyword.trim());
        }

        if (res.isSuccess()) {
            for (User u : res.getData()) {
                tableModel.addRow(new Object[] {u.getId(), u.getUsername(), u.getFullName(),
                        u.getEmail(), u.getRole(), u.isLocked() ? "Đã khóa" : "Hoạt động"});
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + res.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
