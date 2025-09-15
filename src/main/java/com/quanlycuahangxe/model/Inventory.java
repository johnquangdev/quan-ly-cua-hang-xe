/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author Minh
 */
public class Inventory {
    private int inventoryId;
    private int carId;
    private int quantity;
    private int minStockLevel;
    private int maxStockLevel;
    private LocalDate lastRestockedDate;
    private LocalDate nextRestockDate;
    private String status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Inventory() {}
    
    public Inventory(int carId, int quantity, int minStockLevel, int maxStockLevel, 
                    String status, String notes) {
        this.carId = carId;
        this.quantity = quantity;
        this.minStockLevel = minStockLevel;
        this.maxStockLevel = maxStockLevel;
        this.status = status;
        this.notes = notes;
    }

    public int getInventoryId() { return inventoryId; }
    public void setInventoryId(int inventoryId) { this.inventoryId = inventoryId; }
    
    public int getCarId() { return carId; }
    public void setCarId(int carId) { this.carId = carId; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public int getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(int minStockLevel) { this.minStockLevel = minStockLevel; }
    
    public int getMaxStockLevel() { return maxStockLevel; }
    public void setMaxStockLevel(int maxStockLevel) { this.maxStockLevel = maxStockLevel; }
    
    public LocalDate getLastRestockedDate() { return lastRestockedDate; }
    public void setLastRestockedDate(LocalDate lastRestockedDate) { this.lastRestockedDate = lastRestockedDate; }
    
    public LocalDate getNextRestockDate() { return nextRestockDate; }
    public void setNextRestockDate(LocalDate nextRestockDate) { this.nextRestockDate = nextRestockDate; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return "Inventory [ID=" + inventoryId + ", Car=" + carId + ", Quantity=" + quantity + 
               ", Status=" + status + "]";
    }
}