package com.quanlycuahangxe.gui;

import com.quanlycuahangxe.model.Customer;
import com.quanlycuahangxe.model.Invoice;
import com.quanlycuahangxe.service.impl.CustomerServiceImpl;
import com.quanlycuahangxe.service.impl.InvoiceServiceImpl;
import com.quanlycuahangxe.service.interfaces.CustomerService;
import com.quanlycuahangxe.service.interfaces.InvoiceService;
import com.quanlycuahangxe.utils.IconHelper;
import com.quanlycuahangxe.utils.ServiceResult;
import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class CustomerManagementPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnSearch, btnViewInvoices;
    private JTextField txtSearch;

    private final CustomerService customerService = new CustomerServiceImpl();
    private final InvoiceService invoiceService = new InvoiceServiceImpl(); // Để dùng cho lịch sử

    public CustomerManagementPanel() {
        setLayout(new BorderLayout(10, 10));

        // Panel tìm kiếm khách hàng
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

        // Bảng hiển thị danh sách khách hàng
        String[] columns = {"ID", "Họ tên", "Email", "Số điện thoại", "Địa chỉ", "Ngày tạo"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Áp dụng renderer tùy chỉnh cho bảng (để tạo hiệu ứng sọc ngựa vằn)
        CustomTableCellRenderer renderer = new CustomTableCellRenderer();
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        // Panel chứa các nút chức năng
        JPanel panelButtons = new JPanel(new GridLayout(1, 0, 10, 0));
        btnAdd = new JButton("Thêm");
        btnAdd.setIcon(IconHelper.loadIcon("add.png")); 
        btnEdit = new JButton("Sửa");
        btnEdit.setIcon(IconHelper.loadIcon("edit.png")); 
        btnDelete = new JButton("Xóa");
        btnDelete.setIcon(IconHelper.loadIcon("delete.png")); 
        btnViewInvoices = new JButton("Lịch sử mua hàng");
        btnViewInvoices.setIcon(IconHelper.loadIcon("invoice.png")); 
        panelButtons.add(btnAdd);
        panelButtons.add(btnEdit);
        panelButtons.add(btnDelete);
        panelButtons.add(btnViewInvoices);

        add(panelButtons, BorderLayout.SOUTH);

        // Gắn các sự kiện cho nút
        btnAdd.addActionListener(e -> openAddForm());
        btnEdit.addActionListener(e -> openEditForm());
        btnDelete.addActionListener(e -> deleteCustomer());
        btnRefresh.addActionListener(e -> loadCustomers(null)); 
        btnSearch.addActionListener(e -> loadCustomers(txtSearch.getText()));
        btnViewInvoices.addActionListener(e -> handleViewCustomerInvoices());

        // Tải dữ liệu khách hàng ban đầu
        loadCustomers(null);
    }

    private void loadCustomers(String keyword) {
        tableModel.setRowCount(0); 
        ServiceResult<List<Customer>> result;
        if (keyword == null || keyword.trim().isEmpty()) {
            result = customerService.getAllCustomers();
        } else {
            result = customerService.searchCustomers(keyword.trim());
        }

        if (result.isSuccess()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"); 
            for (Customer c : result.getData()) {
                String formattedDate = (c.getCreatedAt() != null) ? formatter.format(c.getCreatedAt()) : "N/A"; 
                tableModel.addRow(new Object[]{c.getId(), c.getFullName(), c.getEmail(),
                    c.getPhone(), c.getAddress(), formattedDate}); 
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + result.getMessage());
        }
    }

    private void openAddForm() {
        // Truyền owner là Window của panel hiện tại
        CustomerForm form = new CustomerForm(SwingUtilities.getWindowAncestor(this),
                "Thêm Khách hàng", null, this); 
        form.setVisible(true);
        loadCustomers(null); 
    }

    // Phương thức làm mới danh sách khách hàng
    public void refreshCustomers() {
        loadCustomers(null);
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
            // Truyền owner là Window của panel hiện tại
            CustomerForm form = new CustomerForm(SwingUtilities.getWindowAncestor(this),
                    "Sửa Khách hàng", result.getData(), this); 
            form.setVisible(true);
            loadCustomers(null); 
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
                loadCustomers(null);
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi: " + result.getMessage());
            }
        }
    }

    private void handleViewCustomerInvoices() {
        int selected = table.getSelectedRow();
        if (selected == -1) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn khách hàng để xem lịch sử mua hàng.");
            return;
        }
        int customerId = (int) tableModel.getValueAt(selected, 0);
        String customerName = (String) tableModel.getValueAt(selected, 1);

        ServiceResult<List<Invoice>> result = invoiceService.getInvoicesByCustomerId(customerId);
        if (result.isSuccess()) {
            List<Invoice> invoices = result.getData();

            // Tạo bảng để hiển thị lịch sử hóa đơn
            String[] columnNames = {"ID HĐ", "Nhân viên", "Ngày tạo", "Tổng tiền"};
            DefaultTableModel invoiceHistoryModel = new DefaultTableModel(columnNames, 0);
            for (Invoice inv : invoices) {
                invoiceHistoryModel.addRow(new Object[]{inv.getId(), inv.getUserName(),
                    inv.getCreatedAt(), inv.getTotalAmount()});
            }
            JTable invoiceHistoryTable = new JTable(invoiceHistoryModel);

            // Hiển thị lịch sử hóa đơn trong một dialog mới
            JDialog historyDialog = new JDialog(SwingUtilities.getWindowAncestor(this),
                    "Lịch sử mua hàng của " + customerName, Dialog.ModalityType.APPLICATION_MODAL); 
            historyDialog.add(new JScrollPane(invoiceHistoryTable)); 
            historyDialog.setSize(600, 400); 
            historyDialog.setLocationRelativeTo(this); 
            historyDialog.setVisible(true); 

        } else {
            JOptionPane.showMessageDialog(this,
                    "Không thể tải lịch sử mua hàng: " + result.getMessage(), "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
