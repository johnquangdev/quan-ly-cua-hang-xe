package com.quanlycuahangxe.dao;

import com.quanlycuahangxe.db.NewConnectPostgres;
import com.quanlycuahangxe.model.Vehicle;
import com.quanlycuahangxe.exception.VehicleNotFoundException;
import com.quanlycuahangxe.exception.DuplicateVehicleException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VehicleManagementDao {
    private final Connection connection;
    
    private static final String INSERT_VEHICLE = 
        "INSERT INTO vehicle (license_plate, vehicle_type, brand, model, manufacture_year, " +
        "owner_name, color, engine_number, chassis_number, registration_date, inspection_expiry) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String SELECT_VEHICLE_BY_LICENSE = 
        "SELECT * FROM vehicle WHERE license_plate = ?";
    
    private static final String SELECT_ALL_VEHICLES = 
        "SELECT * FROM vehicle ORDER BY license_plate";
    
    private static final String UPDATE_VEHICLE = 
        "UPDATE vehicle SET vehicle_type = ?, brand = ?, model = ?, manufacture_year = ?, " +
        "owner_name = ?, color = ?, engine_number = ?, chassis_number = ?, " +
        "registration_date = ?, inspection_expiry = ? WHERE license_plate = ?";
    
    private static final String DELETE_VEHICLE = 
        "DELETE FROM vehicle WHERE license_plate = ?";
    
    private static final String SELECT_VEHICLES_BY_TYPE = 
        "SELECT * FROM vehicle WHERE vehicle_type = ? ORDER BY license_plate";
    
    private static final String SELECT_VEHICLES_BY_OWNER = 
        "SELECT * FROM vehicle WHERE owner_name = ? ORDER BY license_plate";
    
    private static final String SELECT_VEHICLES_BY_BRAND = 
        "SELECT * FROM vehicle WHERE brand = ? ORDER BY license_plate";
    
    private static final String SELECT_VEHICLES_BY_YEAR_RANGE = 
        "SELECT * FROM vehicle WHERE manufacture_year BETWEEN ? AND ? ORDER BY manufacture_year";
    
    private static final String SEARCH_VEHICLES = 
        "SELECT * FROM vehicle WHERE license_plate LIKE ? OR vehicle_type LIKE ? OR " +
        "brand LIKE ? OR model LIKE ? OR owner_name LIKE ? ORDER BY license_plate";
    
    private static final String COUNT_ALL_VEHICLES = 
        "SELECT COUNT(*) FROM vehicle";
    
    private static final String COUNT_VEHICLES_BY_TYPE = 
        "SELECT vehicle_type, COUNT(*) as count FROM vehicle GROUP BY vehicle_type";
    
    private static final String UPDATE_VEHICLE_OWNER = 
        "UPDATE vehicle SET owner_name = ? WHERE license_plate = ?";
    
    private static final String CHECK_VEHICLE_EXISTS = 
        "SELECT COUNT(*) FROM vehicle WHERE license_plate = ?";
    
    private static final String DELETE_ALL_VEHICLES = 
        "DELETE FROM vehicle";
    
    public VehicleManagementDao() {
        this.connection = NewConnectPostgres.getConnection();
    }
    
    public void addVehicle(Vehicle vehicle) throws SQLException, DuplicateVehicleException {
        if (vehicleExists(vehicle.getLicensePlate())) {
            throw new DuplicateVehicleException(
                "Phương tiện với biển số " + vehicle.getLicensePlate() + " đã tồn tại");
        }
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_VEHICLE)) {
            setVehicleParameters(stmt, vehicle);
            stmt.executeUpdate();
        }
    }
    
    public Vehicle getVehicle(String licensePlate) throws SQLException, VehicleNotFoundException {
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_VEHICLE_BY_LICENSE)) {
            stmt.setString(1, licensePlate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extractVehicleFromResultSet(rs);
                } else {
                    throw new VehicleNotFoundException("Không tìm thấy phương tiện với biển số: " + licensePlate);
                }
            }
        }
    }
    
    public List<Vehicle> getAllVehicles() throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_ALL_VEHICLES);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                vehicles.add(extractVehicleFromResultSet(rs));
            }
        }
        return vehicles;
    }
    
    public void updateVehicle(Vehicle vehicle) throws SQLException, VehicleNotFoundException {
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_VEHICLE)) {
            setVehicleParameters(stmt, vehicle);
            stmt.setString(11, vehicle.getLicensePlate());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new VehicleNotFoundException("Không tìm thấy phương tiện với biển số: " + vehicle.getLicensePlate());
            }
        }
    }
    
    public void deleteVehicle(String licensePlate) throws SQLException, VehicleNotFoundException {
        try (PreparedStatement stmt = connection.prepareStatement(DELETE_VEHICLE)) {
            stmt.setString(1, licensePlate);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new VehicleNotFoundException("Không tìm thấy phương tiện với biển số: " + licensePlate);
            }
        }
    }
    
    public List<Vehicle> getVehiclesByType(String vehicleType) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_VEHICLES_BY_TYPE)) {
            stmt.setString(1, vehicleType);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(extractVehicleFromResultSet(rs));
                }
            }
        }
        return vehicles;
    }
    
    public List<Vehicle> getVehiclesByOwner(String ownerName) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_VEHICLES_BY_OWNER)) {
            stmt.setString(1, ownerName);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(extractVehicleFromResultSet(rs));
                }
            }
        }
        return vehicles;
    }
    
    public List<Vehicle> getVehiclesByBrand(String brand) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_VEHICLES_BY_BRAND)) {
            stmt.setString(1, brand);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(extractVehicleFromResultSet(rs));
                }
            }
        }
        return vehicles;
    }
    
    public List<Vehicle> getVehiclesByYearRange(int fromYear, int toYear) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(SELECT_VEHICLES_BY_YEAR_RANGE)) {
            stmt.setInt(1, fromYear);
            stmt.setInt(2, toYear);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(extractVehicleFromResultSet(rs));
                }
            }
        }
        return vehicles;
    }
    
    public List<Vehicle> searchVehicles(String keyword) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String searchPattern = "%" + keyword + "%";
        try (PreparedStatement stmt = connection.prepareStatement(SEARCH_VEHICLES)) {
            for (int i = 1; i <= 5; i++) {
                stmt.setString(i, searchPattern);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(extractVehicleFromResultSet(rs));
                }
            }
        }
        return vehicles;
    }
    
    public int getTotalVehicleCount() throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(COUNT_ALL_VEHICLES);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }
    
    public Map<String, Long> getVehicleCountByType() throws SQLException {
        Map<String, Long> countMap = new HashMap<>();
        try (PreparedStatement stmt = connection.prepareStatement(COUNT_VEHICLES_BY_TYPE);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String vehicleType = rs.getString("vehicle_type");
                long count = rs.getLong("count");
                countMap.put(vehicleType, count);
            }
        }
        return countMap;
    }
    
    public void updateVehicleOwner(String licensePlate, String newOwnerName) 
            throws SQLException, VehicleNotFoundException {
        try (PreparedStatement stmt = connection.prepareStatement(UPDATE_VEHICLE_OWNER)) {
            stmt.setString(1, newOwnerName);
            stmt.setString(2, licensePlate);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new VehicleNotFoundException("Không tìm thấy phương tiện với biển số: " + licensePlate);
            }
        }
    }
    
    public boolean vehicleExists(String licensePlate) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(CHECK_VEHICLE_EXISTS)) {
            stmt.setString(1, licensePlate);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        }
    }
    
    public void clearAllVehicles() throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(DELETE_ALL_VEHICLES)) {
            stmt.executeUpdate();
        }
    }
    
    public List<Vehicle> getVehiclesExpiringInDays(int days) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicle WHERE inspection_expiry IS NOT NULL " +
                    "AND inspection_expiry BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY) " +
                    "ORDER BY inspection_expiry";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, days);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(extractVehicleFromResultSet(rs));
                }
            }
        }
        return vehicles;
    }
    
    public void addVehiclesBatch(List<Vehicle> vehicles) throws SQLException {
        connection.setAutoCommit(false);
        try (PreparedStatement stmt = connection.prepareStatement(INSERT_VEHICLE)) {
            for (Vehicle vehicle : vehicles) {
                if (!vehicleExists(vehicle.getLicensePlate())) {
                    setVehicleParameters(stmt, vehicle);
                    stmt.addBatch();
                }
            }
            stmt.executeBatch();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
    
    private void setVehicleParameters(PreparedStatement stmt, Vehicle vehicle) throws SQLException {
        stmt.setString(1, vehicle.getLicensePlate());
        stmt.setString(2, vehicle.getVehicleType());
        stmt.setString(3, vehicle.getBrand());
        stmt.setString(4, vehicle.getModel());
        stmt.setInt(5, vehicle.getManufactureYear());
        stmt.setString(6, vehicle.getOwnerName());
        stmt.setString(7, vehicle.getColor());
        stmt.setString(8, vehicle.getEngineNumber());
        stmt.setString(9, vehicle.getChassisNumber());
        if (vehicle.getRegistrationDate() != null) {
            stmt.setDate(10, Date.valueOf(vehicle.getRegistrationDate()));
        } else {
            stmt.setNull(10, Types.DATE);
        }
        if (vehicle.getInspectionExpiry() != null) {
            stmt.setDate(11, Date.valueOf(vehicle.getInspectionExpiry()));
        } else {
            stmt.setNull(11, Types.DATE);
        }
    }

    private Vehicle extractVehicleFromResultSet(ResultSet rs) throws SQLException {
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(rs.getString("license_plate"));
        vehicle.setVehicleType(rs.getString("vehicle_type"));
        vehicle.setBrand(rs.getString("brand"));
        vehicle.setModel(rs.getString("model"));
        vehicle.setManufactureYear(rs.getInt("manufacture_year"));
        vehicle.setOwnerName(rs.getString("owner_name"));
        vehicle.setColor(rs.getString("color"));
        vehicle.setEngineNumber(rs.getString("engine_number"));
        vehicle.setChassisNumber(rs.getString("chassis_number"));
        Date regDate = rs.getDate("registration_date");
        if (regDate != null) {
            vehicle.setRegistrationDate(regDate.toLocalDate());
        }
        Date inspectionDate = rs.getDate("inspection_expiry");
        if (inspectionDate != null) {
            vehicle.setInspectionExpiry(inspectionDate.toLocalDate());
        }
        return vehicle;
    }
}