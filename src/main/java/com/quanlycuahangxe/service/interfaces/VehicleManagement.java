/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.quanlycuahangxe.service.interfaces;

import com.quanlycuahangxe.model.Vehicle;
import com.quanlycuahangxe.exception.VehicleNotFoundException;
import com.quanlycuahangxe.exception.DuplicateVehicleException;

import java.util.List;
import java.util.Map;

/**
 *
 * @author Minh
 */
public interface VehicleManagement {
    void addVehicle(Vehicle vehicle) throws DuplicateVehicleException;
    
    Vehicle getVehicle(String licensePlate) throws VehicleNotFoundException;

    void updateVehicle(Vehicle vehicle) throws VehicleNotFoundException;

    void deleteVehicle(String licensePlate) throws VehicleNotFoundException;

    List<Vehicle> getAllVehicles();

    List<Vehicle> getVehiclesByType(String vehicleType);

    List<Vehicle> getVehiclesByOwner(String ownerName);

    List<Vehicle> searchVehicles(String keyword);

    int getTotalVehicleCount();

    Map<String, Long> getVehicleCountByType();

    List<Vehicle> getVehiclesByYearRange(int fromYear, int toYear);

    boolean vehicleExists(String licensePlate);

    void clearAllVehicles();

    List<Vehicle> getSortedVehicles(String sortBy, boolean ascending);

    List<Vehicle> getVehiclesByBrand(String brand);

    void updateVehicleOwner(String licensePlate, String newOwnerName) throws VehicleNotFoundException;
}