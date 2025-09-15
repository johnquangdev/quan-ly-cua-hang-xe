/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.dao;

import com.quanlycuahangxe.model.Customer;
import com.quanlycuahangxe.exception.CustomerNotFoundException;
import com.quanlycuahangxe.exception.DuplicateCustomerException;
import com.quanlycuahangxe.db.NewConnectPostgres;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList; 
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Minh
 */
public class CustomerManagementDao {
    private final Connection conn;

    public CustomerManagementDao() {
        this.conn = NewConnectPostgres.getConnection();
    }

    public Customer createCustomer(Customer customer) throws DuplicateCustomerException {
        try {
            if (existsByEmail(customer.getEmail())) {
                throw DuplicateCustomerException.duplicateEmail(customer.getEmail());
            }
            if (existsByPhoneNumber(customer.getPhoneNumber())) {
                throw DuplicateCustomerException.duplicatePhoneNumber(customer.getPhoneNumber());
            }
            
            String sql = "INSERT INTO customers (first_name, last_name, email, phone_number, address, date_of_birth, gender, is_active, notes, created_at, updated_at) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, customer.getFirstName());
                ps.setString(2, customer.getLastName());
                ps.setString(3, customer.getEmail());
                ps.setString(4, customer.getPhoneNumber());
                ps.setString(5, customer.getAddress());
                
                if (customer.getDateOfBirth() != null) {
                    ps.setDate(6, Date.valueOf(customer.getDateOfBirth()));
                } else {
                    ps.setNull(6, Types.DATE);
                }
                
                ps.setString(7, customer.getGender() != null ? customer.getGender().name() : null);
                ps.setBoolean(8, customer.isActive());
                ps.setString(9, customer.getNotes());
                ps.setTimestamp(10, Timestamp.valueOf(customer.getCreatedAt()));
                ps.setTimestamp(11, Timestamp.valueOf(customer.getUpdatedAt()));
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        customer.setId(rs.getLong("id"));
                    }
                }
            }
            return customer;
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi thêm khách hàng: " + e.getMessage(), e);
        }
    }

    public Customer getCustomerById(Long id) throws CustomerNotFoundException {
        try {
            String sql = "SELECT * FROM customers WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToCustomer(rs);
                    } else {
                        throw CustomerNotFoundException.withId(id);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy khách hàng: " + e.getMessage(), e);
        }
    }

    public Customer updateCustomer(Long id, Customer customer) throws CustomerNotFoundException {
        if (!existsById(id)) {
            throw CustomerNotFoundException.withId(id);
        }
        
        try {
            String sql = "UPDATE customers SET first_name = ?, last_name = ?, email = ?, phone_number = ?, " +
                       "address = ?, date_of_birth = ?, gender = ?, is_active = ?, notes = ?, updated_at = ? " +
                       "WHERE id = ?";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, customer.getFirstName());
                ps.setString(2, customer.getLastName());
                ps.setString(3, customer.getEmail());
                ps.setString(4, customer.getPhoneNumber());
                ps.setString(5, customer.getAddress());
                
                if (customer.getDateOfBirth() != null) {
                    ps.setDate(6, Date.valueOf(customer.getDateOfBirth()));
                } else {
                    ps.setNull(6, Types.DATE);
                }
                
                ps.setString(7, customer.getGender() != null ? customer.getGender().name() : null);
                ps.setBoolean(8, customer.isActive());
                ps.setString(9, customer.getNotes());
                ps.setTimestamp(10, Timestamp.valueOf(customer.getUpdatedAt()));
                ps.setLong(11, id);
                
                ps.executeUpdate();
            }
            return getCustomerById(id);
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi cập nhật khách hàng: " + e.getMessage(), e);
        }
    }

    public void deleteCustomer(Long id) throws CustomerNotFoundException {
        if (!existsById(id)) {
            throw CustomerNotFoundException.withId(id);
        }
        
        try {
            String sql = "DELETE FROM customers WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi xóa khách hàng: " + e.getMessage(), e);
        }
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        try {
            String sql = "SELECT * FROM customers ORDER BY created_at DESC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách khách hàng: " + e.getMessage(), e);
        }
        return customers;
    }

    public List<Customer> getCustomersWithPaging(int page, int size) {
        List<Customer> customers = new ArrayList<>();
        try {
            String sql = "SELECT * FROM customers ORDER BY created_at DESC LIMIT ? OFFSET ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, size);
                ps.setInt(2, (page - 1) * size);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        customers.add(mapResultSetToCustomer(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy khách hàng phân trang: " + e.getMessage(), e);
        }
        return customers;
    }

    public List<Customer> searchByKeyword(String keyword) {
        List<Customer> customers = new ArrayList<>();
        try {
            String sql = "SELECT * FROM customers WHERE LOWER(first_name) LIKE ? OR LOWER(last_name) LIKE ? " +
                       "OR LOWER(email) LIKE ? OR phone_number LIKE ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                String searchPattern = "%" + keyword.toLowerCase() + "%";
                ps.setString(1, searchPattern);
                ps.setString(2, searchPattern);
                ps.setString(3, searchPattern);
                ps.setString(4, searchPattern);
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        customers.add(mapResultSetToCustomer(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm kiếm khách hàng: " + e.getMessage(), e);
        }
        return customers;
    }

    public boolean existsById(Long id) {
        try {
            String sql = "SELECT 1 FROM customers WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean existsByEmail(String email) {
        try {
            String sql = "SELECT 1 FROM customers WHERE email = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        try {
            String sql = "SELECT 1 FROM customers WHERE phone_number = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, phoneNumber);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean existsByEmailAndNotCustomerId(String email, Long excludeId) {
        try {
            String sql = "SELECT 1 FROM customers WHERE email = ? AND id != ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email);
                ps.setLong(2, excludeId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public boolean existsByPhoneNumberAndNotCustomerId(String phoneNumber, Long excludeId) {
        try {
            String sql = "SELECT 1 FROM customers WHERE phone_number = ? AND id != ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, phoneNumber);
                ps.setLong(2, excludeId);
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public long countCustomers() {
        try {
            String sql = "SELECT COUNT(*) FROM customers";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đếm khách hàng: " + e.getMessage(), e);
        }
        return 0;
    }

    public long countActiveCustomers() {
        try {
            String sql = "SELECT COUNT(*) FROM customers WHERE is_active = true";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đếm khách hàng active: " + e.getMessage(), e);
        }
        return 0;
    }

    public long countInactiveCustomers() {
        try {
            String sql = "SELECT COUNT(*) FROM customers WHERE is_active = false";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đếm khách hàng inactive: " + e.getMessage(), e);
        }
        return 0;
    }

    public Optional<Customer> findById(Long id) {
        try {
            String sql = "SELECT * FROM customers WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapResultSetToCustomer(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm khách hàng: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Customer> findByEmail(String email) {
        try {
            String sql = "SELECT * FROM customers WHERE email = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapResultSetToCustomer(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm khách hàng bằng email: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public Optional<Customer> findByPhoneNumber(String phoneNumber) {
        try {
            String sql = "SELECT * FROM customers WHERE phone_number = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, phoneNumber);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(mapResultSetToCustomer(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm khách hàng bằng số điện thoại: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    public List<Customer> findByActiveTrue() {
        List<Customer> customers = new ArrayList<>();
        try {
            String sql = "SELECT * FROM customers WHERE is_active = true ORDER BY created_at DESC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy khách hàng active: " + e.getMessage(), e);
        }
        return customers;
    }

    public List<Customer> findByActiveFalse() {
        List<Customer> customers = new ArrayList<>();
        try {
            String sql = "SELECT * FROM customers WHERE is_active = false ORDER BY created_at DESC";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    customers.add(mapResultSetToCustomer(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy khách hàng inactive: " + e.getMessage(), e);
        }
        return customers;
    }

    public List<Customer> findByFirstNameAndLastName(String firstName, String lastName) {
        List<Customer> customers = new ArrayList<>();
        try {
            String sql = "SELECT * FROM customers WHERE LOWER(first_name) = LOWER(?) AND LOWER(last_name) = LOWER(?)";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, firstName);
                ps.setString(2, lastName);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        customers.add(mapResultSetToCustomer(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm khách hàng bằng tên: " + e.getMessage(), e);
        }
        return customers;
    }

    public List<Customer> findByFirstName(String firstName) {
        List<Customer> customers = new ArrayList<>();
        try {
            String sql = "SELECT * FROM customers WHERE LOWER(first_name) LIKE ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "%" + firstName.toLowerCase() + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        customers.add(mapResultSetToCustomer(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm khách hàng bằng first name: " + e.getMessage(), e);
        }
        return customers;
    }

    public List<Customer> findByLastName(String lastName) {
        List<Customer> customers = new ArrayList<>();
        try {
            String sql = "SELECT * FROM customers WHERE LOWER(last_name) LIKE ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "%" + lastName.toLowerCase() + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        customers.add(mapResultSetToCustomer(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm khách hàng bằng last name: " + e.getMessage(), e);
        }
        return customers;
    }

    public List<Customer> findByDateOfBirthBetween(LocalDate startDate, LocalDate endDate) {
        List<Customer> customers = new ArrayList<>();
        try {
            String sql = "SELECT * FROM customers WHERE date_of_birth BETWEEN ? AND ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDate(1, Date.valueOf(startDate));
                ps.setDate(2, Date.valueOf(endDate));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        customers.add(mapResultSetToCustomer(rs));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi tìm khách hàng theo ngày sinh: " + e.getMessage(), e);
        }
        return customers;
    }

    public long countByCreatedAtYearAndMonth(int year, int month) {
        try {
            String sql = "SELECT COUNT(*) FROM customers WHERE EXTRACT(YEAR FROM created_at) = ? AND EXTRACT(MONTH FROM created_at) = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, year);
                ps.setInt(2, month);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getLong(1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi đếm khách hàng theo tháng: " + e.getMessage(), e);
        }
        return 0;
    }

    public Customer save(Customer customer) throws DuplicateCustomerException {
        if (customer.getId() == null) {
            return createCustomer(customer);
        } else {
            try {
                return updateCustomer(customer.getId(), customer);
            } catch (CustomerNotFoundException e) {
                throw new RuntimeException("Không tìm thấy khách hàng để cập nhật", e);
            }
        }
    }

    public void deleteById(Long id) throws CustomerNotFoundException {
        deleteCustomer(id);
    }

    public List<Customer> findAll() {
        return getAllCustomers();
    }

    public List<Customer> findAllWithPaging(int page, int size) {
        return getCustomersWithPaging(page, size);
    }

    public long countByActiveTrue() {
        return countActiveCustomers();
    }

    public long countByActiveFalse() {
        return countInactiveCustomers();
    }

    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getLong("id"));
        customer.setFirstName(rs.getString("first_name"));
        customer.setLastName(rs.getString("last_name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhoneNumber(rs.getString("phone_number"));
        customer.setAddress(rs.getString("address"));
        
        Date dateOfBirth = rs.getDate("date_of_birth");
        if (dateOfBirth != null) {
            customer.setDateOfBirth(dateOfBirth.toLocalDate());
        }
        
        String gender = rs.getString("gender");
        if (gender != null) {
            customer.setGender(Customer.Gender.valueOf(gender));
        }
        
        customer.setActive(rs.getBoolean("is_active"));
        customer.setNotes(rs.getString("notes"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            customer.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            customer.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return customer;
    }
}