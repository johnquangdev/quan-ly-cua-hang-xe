package com.quanlycuahangxe.model;

import java.time.LocalDateTime;
import java.util.List;

public class Invoice {

    private int id;
    private int customerId;
    private int userId; // nhân viên (user)
    private LocalDateTime createdAt;
    private double totalAmount;

    // Fields để hiển thị (được lấy từ JOIN)
    private String customerName;
    private String userName;

    // Danh sách item kèm tên sản phẩm
    private List<InvoiceItem> items;

    public Invoice() {
    }

    public Invoice(int id, int customerId, int userId, LocalDateTime createdAt, double totalAmount) {
        this.id = id;
        this.customerId = customerId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.totalAmount = totalAmount;
    }

    public Invoice(int id, int customerId, int userId, LocalDateTime createdAt, double totalAmount,
            String customerName, String userName) {
        this(id, customerId, userId, createdAt, totalAmount);
        this.customerName = customerName;
        this.userName = userName;
    }

    // Getters / Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
    }
}
