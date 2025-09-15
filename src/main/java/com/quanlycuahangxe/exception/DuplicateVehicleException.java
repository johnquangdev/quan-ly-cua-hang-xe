/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.exception;

/**
 *
 * @author Minh
 */
public class DuplicateVehicleException extends Exception {
    private String licensePlate;

    public DuplicateVehicleException() {
        super("Phương tiện đã tồn tại trong hệ thống");
    }
    
    public DuplicateVehicleException(String message) {
        super(message);
    }

    public DuplicateVehicleException(String message, String licensePlate) {
        super(message);
        this.licensePlate = licensePlate;
    }

    public DuplicateVehicleException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateVehicleException(String message, String licensePlate, Throwable cause) {
        super(message, cause);
        this.licensePlate = licensePlate;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
    
    @Override
    public String toString() {
        if (licensePlate != null) {
            return super.toString() + " - Biển số trùng: " + licensePlate;
        }
        return super.toString();
    }
}