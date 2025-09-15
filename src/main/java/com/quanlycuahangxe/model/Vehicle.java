package com.quanlycuahangxe.model;

import java.time.LocalDate;
import java.util.Objects;

public class Vehicle {
    private String licensePlate;    // Biển số xe
    private String vehicleType;     // Loại xe (ô tô, xe máy, xe tải, etc.)
    private String brand;           // Hãng xe
    private String model;           // Model xe
    private int manufactureYear;    // Năm sản xuất
    private String ownerName;       // Tên chủ sở hữu
    private String color;           // Màu xe
    private String engineNumber;    // Số máy
    private String chassisNumber;   // Số khung
    private LocalDate registrationDate; // Ngày đăng ký
    private LocalDate inspectionExpiry; // Ngày hết hạn đăng kiểm

    public Vehicle() {}

    public Vehicle(String licensePlate, String vehicleType, String brand, 
                   String model, int manufactureYear, String ownerName) {
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.brand = brand;
        this.model = model;
        this.manufactureYear = manufactureYear;
        this.ownerName = ownerName;
        this.registrationDate = LocalDate.now();
    }

    public Vehicle(String licensePlate, String vehicleType, String brand, String model,
                   int manufactureYear, String ownerName, String color, String engineNumber,
                   String chassisNumber, LocalDate registrationDate, LocalDate inspectionExpiry) {
        this.licensePlate = licensePlate;
        this.vehicleType = vehicleType;
        this.brand = brand;
        this.model = model;
        this.manufactureYear = manufactureYear;
        this.ownerName = ownerName;
        this.color = color;
        this.engineNumber = engineNumber;
        this.chassisNumber = chassisNumber;
        this.registrationDate = registrationDate;
        this.inspectionExpiry = inspectionExpiry;
    }

    public String getLicensePlate() {
        return licensePlate;
    }
    
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }
    
    public String getVehicleType() {
        return vehicleType;
    }
    
    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }
    
    public String getBrand() {
        return brand;
    }
    
    public void setBrand(String brand) {
        this.brand = brand;
    }
    
    public String getModel() {
        return model;
    }
    
    public void setModel(String model) {
        this.model = model;
    }
    
    public int getManufactureYear() {
        return manufactureYear;
    }
    
    public void setManufactureYear(int manufactureYear) {
        this.manufactureYear = manufactureYear;
    }
    
    public String getOwnerName() {
        return ownerName;
    }
    
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public String getEngineNumber() {
        return engineNumber;
    }
    
    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }
    
    public String getChassisNumber() {
        return chassisNumber;
    }
    
    public void setChassisNumber(String chassisNumber) {
        this.chassisNumber = chassisNumber;
    }
    
    public LocalDate getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public LocalDate getInspectionExpiry() {
        return inspectionExpiry;
    }
    
    public void setInspectionExpiry(LocalDate inspectionExpiry) {
        this.inspectionExpiry = inspectionExpiry;
    }

    public boolean isInspectionExpired() {
        return inspectionExpiry != null && inspectionExpiry.isBefore(LocalDate.now());
    }

    public int getVehicleAge() {
        return LocalDate.now().getYear() - manufactureYear;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vehicle vehicle = (Vehicle) obj;
        return Objects.equals(licensePlate, vehicle.licensePlate);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(licensePlate);
    }
    
    @Override
    public String toString() {
        return String.format("Vehicle{licensePlate='%s', type='%s', brand='%s', model='%s', year=%d, owner='%s'}",
                licensePlate, vehicleType, brand, model, manufactureYear, ownerName);
    }
}