/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.exception;

/**
 *
 * @author Minh
 */
public class VehicleNotFoundException extends Exception {
    private String licensePlate;

    public VehicleNotFoundException() {
        super("Không tìm thấy phương tiện");
    }

    public VehicleNotFoundException(String message) {
        super(message);
    }

    public VehicleNotFoundException(String message, String licensePlate) {
        super(message);
        this.licensePlate = licensePlate;
    }

    public VehicleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public VehicleNotFoundException(String message, String licensePlate, Throwable cause) {
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
            return super.toString() + " - Biển số: " + licensePlate;
        }
        return super.toString();
    }
}