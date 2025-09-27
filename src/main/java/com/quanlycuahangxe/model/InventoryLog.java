package com.quanlycuahangxe.model;

import java.time.LocalDateTime;

public class InventoryLog {

    private int id;
    private int productId;
    private int changeAmount;
    private String reason;
    private LocalDateTime createdAt;
    // Lưu ý: Bảng của bạn chưa có user_id, tôi sẽ tạm thời bỏ qua.
    // Nếu cần追적 ai đã thay đổi, chúng ta có thể thêm cột user_id sau.

    public InventoryLog() {
    }

    public InventoryLog(int productId, int changeAmount, String reason) {
        this.productId = productId;
        this.changeAmount = changeAmount;
        this.reason = reason;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount(int changeAmount) {
        this.changeAmount = changeAmount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
