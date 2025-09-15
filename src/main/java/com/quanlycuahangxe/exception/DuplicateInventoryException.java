/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.exception;

/**
 *
 * @author Minh
 */
public class DuplicateInventoryException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public DuplicateInventoryException() {
        super("Duplicate inventory record");
    }
    
    public DuplicateInventoryException(String message) {
        super(message);
    }
    
    public DuplicateInventoryException(int inventoryId) {
        super("Inventory record with ID " + inventoryId + " already exists");
    }
    
    public DuplicateInventoryException(int carId, boolean byCarId) {
        super("Inventory record for car ID " + carId + " already exists");
    }
    
    public static DuplicateInventoryException duplicateInventory(int inventoryId) {
        return new DuplicateInventoryException("Inventory record with ID " + inventoryId + " already exists");
    }
    
    public static DuplicateInventoryException duplicateCarInventory(int carId) {
        return new DuplicateInventoryException("Inventory record for car ID " + carId + " already exists");
    }
}