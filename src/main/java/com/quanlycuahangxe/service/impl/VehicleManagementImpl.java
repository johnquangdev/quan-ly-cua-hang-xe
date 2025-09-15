/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.quanlycuahangxe.service.impl;

import com.quanlycuahangxe.model.Vehicle;
import com.quanlycuahangxe.service.interfaces.VehicleManagement;
import com.quanlycuahangxe.exception.VehicleNotFoundException;
import com.quanlycuahangxe.exception.DuplicateVehicleException;

import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @author Minh
 */

public class VehicleManagementImpl implements VehicleManagement {
    private final Map<String, Vehicle> vehicles;
    private static final String LICENSE_PLATE_PATTERN = "^[0-9]{2}[A-Z]{1,2}-[0-9]{4,5}$";
    
    public VehicleManagementImpl() {
        this.vehicles = new HashMap<>();
    }
    
    @Override
    public void addVehicle(Vehicle vehicle) throws DuplicateVehicleException {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle không thể null");
        }
        if (vehicles.containsKey(vehicle.getLicensePlate())) {
            throw new DuplicateVehicleException(
                "Phương tiện với biển số " + vehicle.getLicensePlate() + " đã tồn tại"
            );
        }
        if (!isValidLicensePlate(vehicle.getLicensePlate())) {
            throw new IllegalArgumentException("Biển số xe không hợp lệ: " + vehicle.getLicensePlate());
        }
        vehicles.put(vehicle.getLicensePlate(), vehicle);
    }
    
    @Override
    public Vehicle getVehicle(String licensePlate) throws VehicleNotFoundException {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("Biển số xe không thể null hoặc rỗng");
        }
        Vehicle vehicle = vehicles.get(licensePlate);
        if (vehicle == null) {
            throw new VehicleNotFoundException("Không tìm thấy phương tiện với biển số: " + licensePlate);
        }
        return vehicle;
    }
    
    @Override
    public void updateVehicle(Vehicle vehicle) throws VehicleNotFoundException {
        if (vehicle == null) {
            throw new IllegalArgumentException("Vehicle không thể null");
        }
        if (!vehicles.containsKey(vehicle.getLicensePlate())) {
            throw new VehicleNotFoundException(
                "Không tìm thấy phương tiện với biển số: " + vehicle.getLicensePlate()
            );
        }
        vehicles.put(vehicle.getLicensePlate(), vehicle);
    }
    
    @Override
    public void deleteVehicle(String licensePlate) throws VehicleNotFoundException {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("Biển số xe không thể null hoặc rỗng");
        }
        
        Vehicle removedVehicle = vehicles.remove(licensePlate);
        if (removedVehicle == null) {
            throw new VehicleNotFoundException("Không tìm thấy phương tiện với biển số: " + licensePlate);
        }
    }
    
    @Override
    public List<Vehicle> getAllVehicles() {
        return new ArrayList<>(vehicles.values());
    }
    
    @Override
    public List<Vehicle> getVehiclesByType(String vehicleType) {
        if (vehicleType == null || vehicleType.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return vehicles.values().stream()
                .filter(vehicle -> vehicleType.equalsIgnoreCase(vehicle.getVehicleType()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Vehicle> getVehiclesByOwner(String ownerName) {
        if (ownerName == null || ownerName.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return vehicles.values().stream()
                .filter(vehicle -> ownerName.equalsIgnoreCase(vehicle.getOwnerName()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Vehicle> searchVehicles(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllVehicles();
        }
        String lowerKeyword = keyword.toLowerCase();
        return vehicles.values().stream()
                .filter(vehicle -> 
                    vehicle.getLicensePlate().toLowerCase().contains(lowerKeyword) ||
                    vehicle.getVehicleType().toLowerCase().contains(lowerKeyword) ||
                    vehicle.getBrand().toLowerCase().contains(lowerKeyword) ||
                    vehicle.getModel().toLowerCase().contains(lowerKeyword) ||
                    vehicle.getOwnerName().toLowerCase().contains(lowerKeyword)
                )
                .collect(Collectors.toList());
    }
    
    @Override
    public int getTotalVehicleCount() {
        return vehicles.size();
    }
    
    @Override
    public Map<String, Long> getVehicleCountByType() {
        return vehicles.values().stream()
                .collect(Collectors.groupingBy(
                    Vehicle::getVehicleType,
                    Collectors.counting()
                ));
    }
    
    @Override
    public List<Vehicle> getVehiclesByYearRange(int fromYear, int toYear) {
        if (fromYear > toYear) {
            throw new IllegalArgumentException("Năm bắt đầu không thể lớn hơn năm kết thúc");
        }
        return vehicles.values().stream()
                .filter(vehicle -> {
                    int year = vehicle.getManufactureYear();
                    return year >= fromYear && year <= toYear;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean vehicleExists(String licensePlate) {
        return licensePlate != null && vehicles.containsKey(licensePlate);
    }
    
    @Override
    public void clearAllVehicles() {
        vehicles.clear();
    }
    
    @Override
    public List<Vehicle> getSortedVehicles(String sortBy, boolean ascending) {
        List<Vehicle> vehicleList = new ArrayList<>(vehicles.values());
        Comparator<Vehicle> comparator;
        comparator = switch (sortBy.toLowerCase()) {
            case "licenseplate" -> Comparator.comparing(Vehicle::getLicensePlate);
            case "type" -> Comparator.comparing(Vehicle::getVehicleType);
            case "brand" -> Comparator.comparing(Vehicle::getBrand);
            case "model" -> Comparator.comparing(Vehicle::getModel);
            case "year" -> Comparator.comparing(Vehicle::getManufactureYear);
            case "owner" -> Comparator.comparing(Vehicle::getOwnerName);
            default -> Comparator.comparing(Vehicle::getLicensePlate);
        };
        if (!ascending) {
            comparator = comparator.reversed();
        }
        vehicleList.sort(comparator);
        return vehicleList;
    }
    
    @Override
    public List<Vehicle> getVehiclesByBrand(String brand) {
        if (brand == null || brand.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return vehicles.values().stream()
                .filter(vehicle -> brand.equalsIgnoreCase(vehicle.getBrand()))
                .collect(Collectors.toList());
    }
    
    @Override
    public void updateVehicleOwner(String licensePlate, String newOwnerName) 
            throws VehicleNotFoundException {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            throw new IllegalArgumentException("Biển số xe không thể null hoặc rỗng");
        }
        if (newOwnerName == null || newOwnerName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tên chủ sở hữu mới không thể null hoặc rỗng");
        }
        Vehicle vehicle = getVehicle(licensePlate);
        vehicle.setOwnerName(newOwnerName.trim());
        vehicles.put(licensePlate, vehicle);
    }

    private boolean isValidLicensePlate(String licensePlate) {
        if (licensePlate == null || licensePlate.trim().isEmpty()) {
            return false;
        }
        return licensePlate.matches(LICENSE_PLATE_PATTERN);
    }

    public String exportToCSV() {
        StringBuilder csv = new StringBuilder();
        csv.append("Biển số,Loại xe,Hãng,Model,Năm sản xuất,Chủ sở hữu\n");
        for (Vehicle vehicle : vehicles.values()) {
            csv.append(String.format("%s,%s,%s,%s,%d,%s\n",
                vehicle.getLicensePlate(),
                vehicle.getVehicleType(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getManufactureYear(),
                vehicle.getOwnerName()
            ));
        }
        return csv.toString();
    }

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalVehicles", vehicles.size());
        stats.put("vehiclesByType", getVehicleCountByType());
        Map<String, Long> brandStats = vehicles.values().stream()
                .collect(Collectors.groupingBy(
                    Vehicle::getBrand,
                    Collectors.counting()
                ));
        stats.put("vehiclesByBrand", brandStats);
        OptionalInt oldestYear = vehicles.values().stream()
                .mapToInt(Vehicle::getManufactureYear)
                .min();
        OptionalInt newestYear = vehicles.values().stream()
                .mapToInt(Vehicle::getManufactureYear)
                .max();
        if (oldestYear.isPresent()) {
            stats.put("oldestVehicleYear", oldestYear.getAsInt());
        }
        if (newestYear.isPresent()) {
            stats.put("newestVehicleYear", newestYear.getAsInt());
        }
        return stats;
    }
}