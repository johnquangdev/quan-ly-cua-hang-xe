package com.quanlycuahangxe.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.quanlycuahangxe.model.Invoice;
import com.quanlycuahangxe.model.InvoiceItem;
import com.quanlycuahangxe.service.impl.InvoiceServiceImpl;
import com.quanlycuahangxe.service.interfaces.InvoiceService;
import com.quanlycuahangxe.utils.CurrencyHelper;
import com.quanlycuahangxe.utils.ServiceResult;

public class InvoiceDetailDialog extends JDialog {

    private JLabel lblId, lblCustomer, lblStaff, lblCreatedAt, lblTotal;
    private JTable tableItems;
    private DefaultTableModel tableModel;

    private InvoiceService invoiceService = new InvoiceServiceImpl();

    public InvoiceDetailDialog(int invoiceId) {
        setTitle("Chi tiết hóa đơn #" + invoiceId);
        setSize(650, 450);
        setLocationRelativeTo(null);
        setModal(true);

        setLayout(new BorderLayout());

        // Panel thông tin hóa đơn
        JPanel panelInfo = new JPanel(new GridLayout(5, 2, 5, 5));
        panelInfo.setBorder(BorderFactory.createTitledBorder("Thông tin hóa đơn"));

        panelInfo.add(new JLabel("Mã hóa đơn:"));
        lblId = new JLabel();
        panelInfo.add(lblId);

        panelInfo.add(new JLabel("Khách hàng:"));
        lblCustomer = new JLabel();
        panelInfo.add(lblCustomer);

        panelInfo.add(new JLabel("Nhân viên:"));
        lblStaff = new JLabel();
        panelInfo.add(lblStaff);

        panelInfo.add(new JLabel("Ngày tạo:"));
        lblCreatedAt = new JLabel();
        panelInfo.add(lblCreatedAt);

        panelInfo.add(new JLabel("Tổng tiền:"));
        lblTotal = new JLabel();
        panelInfo.add(lblTotal);

        add(panelInfo, BorderLayout.NORTH);

        // Bảng chi tiết sản phẩm
        tableModel = new DefaultTableModel(
                new Object[]{"Sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tableItems = new JTable(tableModel);

        // Áp dụng CurrencyRenderer cho cột "Đơn giá" (index 2) và "Thành tiền" (index 3)
        CurrencyRenderer currencyRenderer = new CurrencyRenderer();
        tableItems.getColumnModel().getColumn(2).setCellRenderer(currencyRenderer);
        tableItems.getColumnModel().getColumn(3).setCellRenderer(currencyRenderer);

        add(new JScrollPane(tableItems), BorderLayout.CENTER);

        // Nút đóng
        JButton btnClose = new JButton("Đóng");
        btnClose.addActionListener(e -> dispose());

        JPanel panelBtn = new JPanel();
        panelBtn.add(btnClose);
        add(panelBtn, BorderLayout.SOUTH);

        // Tải dữ liệu hóa đơn
        loadInvoiceData(invoiceId);
    }

    private void loadInvoiceData(int invoiceId) {
        ServiceResult<Invoice> resInvoice = invoiceService.getInvoiceDetails(invoiceId);
        if (!resInvoice.isSuccess()) {
            JOptionPane.showMessageDialog(this, resInvoice.getMessage());
            dispose();
            return;
        }

        Invoice invoice = resInvoice.getData();
        lblId.setText(String.valueOf(invoice.getId()));
        lblCustomer.setText(invoice.getCustomerName() != null ? invoice.getCustomerName()
                : String.valueOf(invoice.getCustomerId()));
        lblStaff.setText(invoice.getUserName() != null ? invoice.getUserName()
                : String.valueOf(invoice.getUserId()));
        lblCreatedAt
                .setText(invoice.getCreatedAt() != null ? invoice.getCreatedAt().toString() : "");
        lblTotal.setText(CurrencyHelper.formatVND(invoice.getTotalAmount()));

        // Tải các mặt hàng của hóa đơn (Invoice.items đã được set trong DAO)
        tableModel.setRowCount(0);
        List<InvoiceItem> items = invoice.getItems();
        if (items != null) {
            for (InvoiceItem item : items) {
                tableModel.addRow(new Object[]{
                    item.getProductName() != null ? item.getProductName()
                    : String.valueOf(item.getProductId()),
                    item.getQuantity(), item.getPrice(), item.getQuantity() * item.getPrice()});
            }
        }
    }
}
