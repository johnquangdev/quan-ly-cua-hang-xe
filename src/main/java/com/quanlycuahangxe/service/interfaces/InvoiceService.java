package com.quanlycuahangxe.service.interfaces;

import java.awt.Component;
import java.awt.Window;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.quanlycuahangxe.model.Invoice;
import com.quanlycuahangxe.model.InvoiceItem;
import com.quanlycuahangxe.utils.ServiceResult;

public interface InvoiceService {

    ServiceResult<Invoice> createInvoice(int customerId, int staffId);
    ServiceResult<Invoice> createInvoice(Connection conn, int customerId, int staffId) throws SQLException;

    ServiceResult<Invoice> getInvoiceById(int id);
    ServiceResult<Invoice> getInvoiceById(Connection conn, int id) throws SQLException;

    ServiceResult<List<Invoice>> getAllInvoices();
    ServiceResult<List<Invoice>> getAllInvoices(Connection conn) throws SQLException;

    ServiceResult<InvoiceItem> addInvoiceItem(int invoiceId, int productId, int quantity,double price);
    ServiceResult<InvoiceItem> addInvoiceItem(Connection conn, int invoiceId, int productId, int quantity,double price) throws SQLException;

    ServiceResult<Void> removeInvoiceItem(int invoiceItemId);
    ServiceResult<Void> removeInvoiceItem(Connection conn, int invoiceItemId) throws SQLException;

    ServiceResult<Double> calculateTotal(int invoiceId);
    ServiceResult<Double> calculateTotal(Connection conn, int invoiceId) throws SQLException;

    ServiceResult<Void> deleteInvoice(int id);
    ServiceResult<Void> deleteInvoice(Connection conn, int id) throws SQLException;

    Window getWindowAncestor(Component component);

    ServiceResult<Invoice> getInvoiceDetails(int invoiceId);
    ServiceResult<Invoice> getInvoiceDetails(Connection conn, int invoiceId) throws SQLException;

    ServiceResult<List<Invoice>> searchInvoicesByCustomerEmail(String email);
    ServiceResult<List<Invoice>> searchInvoicesByCustomerEmail(Connection conn, String email) throws SQLException;

    ServiceResult<List<Invoice>> getInvoicesByCustomerId(int customerId);
    ServiceResult<List<Invoice>> getInvoicesByCustomerId(Connection conn, int customerId) throws SQLException;

    ServiceResult<List<Invoice>> searchInvoices(String keyword);
    ServiceResult<List<Invoice>> searchInvoices(Connection conn, String keyword) throws SQLException;

    ServiceResult<Invoice> updateInvoice(int invoiceId, int customerId, int staffId,List<InvoiceItem> newItems);
    ServiceResult<Invoice> updateInvoice(Connection conn, int invoiceId, int customerId, int staffId,List<InvoiceItem> newItems) throws SQLException;
}
