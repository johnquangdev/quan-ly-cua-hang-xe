/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this
 * license Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.gui;

/**
 *
 * @author gunnguyen
 */
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

import com.quanlycuahangxe.model.Invoice;
import com.quanlycuahangxe.model.User;
import com.quanlycuahangxe.service.impl.InvoiceServiceImpl;
import com.quanlycuahangxe.service.interfaces.InvoiceService;
import com.quanlycuahangxe.utils.IconHelper;
import com.quanlycuahangxe.utils.ServiceResult;

public class InvoiceManagementPanel extends JPanel {

    private JTable tableInvoices;
    private DefaultTableModel tableModel;
    private JButton btnNewInvoice, btnEditInvoice, btnDeleteInvoice, btnViewInvoice;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnRefresh;

    private InvoiceService invoiceService = new InvoiceServiceImpl();
    private User currentUser; 

    public InvoiceManagementPanel(User currentUser) {
        this.currentUser = currentUser;
        setLayout(new BorderLayout(10, 10));

        // Panel tìm kiếm
        JPanel panelSearch = new JPanel(new FlowLayout(FlowLayout.LEFT));
        txtSearch = new JTextField();
        txtSearch.setPreferredSize(new Dimension(300, 30));
        btnSearch = new JButton("Tìm kiếm");
        btnSearch.setIcon(IconHelper.loadIcon("search.png"));
        btnRefresh = new JButton("Làm mới");
        btnRefresh.setIcon(IconHelper.loadIcon("refresh.png"));

        panelSearch.add(new JLabel("Tìm kiếm:"));
        panelSearch.add(txtSearch);
        panelSearch.add(btnSearch);
        panelSearch.add(btnRefresh);
        add(panelSearch, BorderLayout.NORTH);

        // Table 
        tableModel = new DefaultTableModel(
                new Object[]{"ID", "Khách hàng", "Nhân viên", "Ngày tạo", "Tổng"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableInvoices = new JTable(tableModel);
        add(new JScrollPane(tableInvoices), BorderLayout.CENTER);

        // Apply custom renderer for zebra striping
        CustomTableCellRenderer renderer = new CustomTableCellRenderer();
        for (int i = 0; i < tableInvoices.getColumnCount(); i++) {
            tableInvoices.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // Apply CurrencyRenderer to the "Tổng" column (index 4)
        tableInvoices.getColumnModel().getColumn(4).setCellRenderer(new CurrencyRenderer());

        // Buttons 
        JPanel panelBtn = new JPanel(new GridLayout(1, 0, 10, 0));
        btnNewInvoice = new JButton("Tạo hợp đồng");
        btnNewInvoice.setIcon(IconHelper.loadIcon("add.png"));
        btnEditInvoice = new JButton("Sửa hợp đồng");
        btnEditInvoice.setIcon(IconHelper.loadIcon("edit.png"));
        btnDeleteInvoice = new JButton("Xóa hợp đồng");
        btnDeleteInvoice.setIcon(IconHelper.loadIcon("delete.png"));
        btnViewInvoice = new JButton("Xem chi tiết");
        btnViewInvoice.setIcon(IconHelper.loadIcon("view.png"));

        panelBtn.add(btnNewInvoice);
        panelBtn.add(btnEditInvoice);
        panelBtn.add(btnDeleteInvoice);
        panelBtn.add(btnViewInvoice);

        add(panelBtn, BorderLayout.SOUTH);

        // Action listeners 
        btnNewInvoice.addActionListener(e -> {
            InvoiceFormDialog dialog = new InvoiceFormDialog(currentUser);
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadInvoices(); // load lại bảng sau khi tạo hợp đồng
            }
        });

        btnEditInvoice.addActionListener(e -> {
            int row = tableInvoices.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Chọn hợp đồng để sửa");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            InvoiceFormDialog dialog = new InvoiceFormDialog(this, currentUser, id); // mở form với
            // dữ liệu sẵn
            dialog.setVisible(true);
            if (dialog.isSaved()) {
                loadInvoices();
            }
        });

        btnDeleteInvoice.addActionListener(e -> {
            int row = tableInvoices.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Chọn hợp đồng để xóa");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            int conf = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn xóa hợp đồng này?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (conf == JOptionPane.YES_OPTION) {
                invoiceService.deleteInvoice(id);
                loadInvoices();
            }
        });

        btnViewInvoice.addActionListener(e -> {
            int row = tableInvoices.getSelectedRow();
            if (row < 0) {
                JOptionPane.showMessageDialog(this, "Chọn hợp đồng để xem chi tiết");
                return;
            }
            int id = (int) tableModel.getValueAt(row, 0);
            InvoiceDetailDialog detail = new InvoiceDetailDialog(id);
            detail.setVisible(true);
        });

        btnSearch.addActionListener(e -> loadInvoices(txtSearch.getText()));
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            loadInvoices(null);
        });

        loadInvoices();
    }

    public void loadInvoices() {
        loadInvoices(null);
    }

    private void loadInvoices(String keyword) {
        tableModel.setRowCount(0);
        ServiceResult<List<Invoice>> res;
        if (keyword == null || keyword.trim().isEmpty()) {
            res = invoiceService.getAllInvoices();
        } else {
            res = invoiceService.searchInvoices(keyword.trim());
        }

        if (res.isSuccess()) {
            for (Invoice inv : res.getData()) {
                tableModel.addRow(new Object[]{inv.getId(), inv.getCustomerName(),
                    inv.getUserName(), inv.getCreatedAt(), inv.getTotalAmount()});
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + res.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
