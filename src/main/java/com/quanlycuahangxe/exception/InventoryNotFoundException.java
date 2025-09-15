/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.exception;

/**
 *
 * @author Minh
 */
public class InventoryNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public InventoryNotFoundException() {
        super("Inventory record not found");
    }
    
    public InventoryNotFoundException(String message) {
        super(message);
    }
    
    public InventoryNotFoundException(int inventoryId) {
        super("Inventory record with ID " + inventoryId + " not found");
    }
    
    public InventoryNotFoundException(int carId, boolean byCarId) {
        super("Inventory record for car ID " + carId + " not found");
    }
    
    public static InventoryNotFoundException withId(int inventoryId) {
        return new InventoryNotFoundException("Inventory record with ID " + inventoryId + " not found");
    }
    
    public static InventoryNotFoundException withCarId(int carId) {
        return new InventoryNotFoundException("Inventory record for car ID " + carId + " not found");
    }
}