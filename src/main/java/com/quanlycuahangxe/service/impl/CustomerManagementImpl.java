/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.service.impl;

import com.quanlycuahangxe.model.Customer;
import com.quanlycuahangxe.service.interfaces.CustomerManagement;
import com.quanlycuahangxe.dao.CustomerManagementDao;
import com.quanlycuahangxe.exception.CustomerNotFoundException;
import com.quanlycuahangxe.exception.DuplicateCustomerException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Minh
 */
public class CustomerManagementImpl implements CustomerManagement {
    private final CustomerManagementDao customerDao;

    public CustomerManagementImpl() {
        this.customerDao = new CustomerManagementDao();
    }

    public CustomerManagementImpl(CustomerManagementDao customerDao) {
        this.customerDao = customerDao;
    }

    @Override
    public Customer createCustomer(Customer customer) throws DuplicateCustomerException {
        validateCustomer(customer);
        
        if (customerDao.existsByEmail(customer.getEmail())) {
            throw DuplicateCustomerException.duplicateEmail(customer.getEmail());
        }
        
        if (customerDao.existsByPhoneNumber(customer.getPhoneNumber())) {
            throw DuplicateCustomerException.duplicatePhoneNumber(customer.getPhoneNumber());
        }
        
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        customer.setActive(true);
        
        return customerDao.save(customer);
    }

    @Override
    public Customer updateCustomer(Long customerId, Customer customerDetails) 
            throws CustomerNotFoundException, DuplicateCustomerException {
        
        Customer existingCustomer = customerDao.findById(customerId)
            .orElseThrow(() -> CustomerNotFoundException.withId(customerId));
        
        validateCustomer(customerDetails);
        
        if (customerDao.existsByEmailAndNotCustomerId(customerDetails.getEmail(), customerId)) {
            throw DuplicateCustomerException.duplicateEmail(customerDetails.getEmail());
        }
        
        if (customerDao.existsByPhoneNumberAndNotCustomerId(customerDetails.getPhoneNumber(), customerId)) {
            throw DuplicateCustomerException.duplicatePhoneNumber(customerDetails.getPhoneNumber());
        }
        
        existingCustomer.setFirstName(customerDetails.getFirstName());
        existingCustomer.setLastName(customerDetails.getLastName());
        existingCustomer.setEmail(customerDetails.getEmail());
        existingCustomer.setPhoneNumber(customerDetails.getPhoneNumber());
        existingCustomer.setAddress(customerDetails.getAddress());
        existingCustomer.setDateOfBirth(customerDetails.getDateOfBirth());
        existingCustomer.setGender(customerDetails.getGender());
        existingCustomer.setNotes(customerDetails.getNotes());
        existingCustomer.setUpdatedAt(LocalDateTime.now());
        
        return customerDao.save(existingCustomer);
    }

    @Override
    public Optional<Customer> findCustomerById(Long customerId) {
        return customerDao.findById(customerId);
    }

    @Override
    public Customer getCustomerById(Long customerId) throws CustomerNotFoundException {
        return customerDao.findById(customerId)
            .orElseThrow(() -> CustomerNotFoundException.withId(customerId));
    }

    @Override
    public Optional<Customer> findCustomerByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        return customerDao.findByEmail(email.trim().toLowerCase());
    }

    @Override
    public Optional<Customer> findCustomerByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }
        return customerDao.findByPhoneNumber(phoneNumber.trim());
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerDao.findAll();
    }

    @Override
    public List<Customer> getCustomersWithPaging(int page, int size) {
        if (page < 1) {
            throw new IllegalArgumentException("Số trang phải lớn hơn 0");
        }
        if (size < 1) {
            throw new IllegalArgumentException("Kích thước trang phải lớn hơn 0");
        }
        return customerDao.findAllWithPaging(page, size);
    }

    @Override
    public List<Customer> getActiveCustomers() {
        return customerDao.findByActiveTrue();
    }

    @Override
    public List<Customer> getInactiveCustomers() {
        return customerDao.findByActiveFalse();
    }

    @Override
    public List<Customer> searchCustomers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllCustomers();
        }
        return customerDao.searchByKeyword(keyword.trim());
    }

    @Override
    public List<Customer> findCustomersByName(String firstName, String lastName) {
        if (firstName != null && lastName != null) {
            return customerDao.findByFirstNameAndLastName(firstName.trim(), lastName.trim());
        } else if (firstName != null) {
            return customerDao.findByFirstName(firstName.trim());
        } else if (lastName != null) {
            return customerDao.findByLastName(lastName.trim());
        }
        return getAllCustomers();
    }

    @Override
    public List<Customer> findCustomersByBirthDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Ngày bắt đầu và ngày kết thúc không được để trống");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc");
        }
        return customerDao.findByDateOfBirthBetween(startDate, endDate);
    }

    @Override
    public void deactivateCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerDao.findById(customerId)
            .orElseThrow(() -> CustomerNotFoundException.withId(customerId));

        customer.setActive(false);
        customer.setUpdatedAt(LocalDateTime.now());

        try {
            customerDao.save(customer);
        } catch (DuplicateCustomerException e) {
            throw new RuntimeException("Lỗi không mong muốn khi deactivate khách hàng: " + e.getMessage(), e);
        }
    }

    @Override
    public void activateCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerDao.findById(customerId)
            .orElseThrow(() -> CustomerNotFoundException.withId(customerId));
        customer.setActive(true);
        customer.setUpdatedAt(LocalDateTime.now());
        try {
            customerDao.save(customer);
        } catch (DuplicateCustomerException e) {
            throw new RuntimeException("Lỗi không mong muốn khi activate khách hàng: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteCustomer(Long customerId) throws CustomerNotFoundException {
        if (!customerDao.existsById(customerId)) {
            throw CustomerNotFoundException.withId(customerId);
        }
        customerDao.deleteById(customerId);
    }

    @Override
    public int deleteMultipleCustomers(List<Long> customerIds) {
        if (customerIds == null || customerIds.isEmpty()) {
            return 0;
        }
        
        int deletedCount = 0;
        for (Long customerId : customerIds) {
            try {
                deleteCustomer(customerId);
                deletedCount++;
            } catch (CustomerNotFoundException e) {
                System.err.println("Không thể xóa khách hàng ID: " + customerId + " - " + e.getMessage());
            }
        }
        return deletedCount;
    }

    @Override
    public long countCustomers() {
        return customerDao.countCustomers();
    }

    @Override
    public long countActiveCustomers() {
        return customerDao.countByActiveTrue();
    }

    @Override
    public long countInactiveCustomers() {
        return customerDao.countByActiveFalse();
    }

    @Override
    public boolean existsByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return customerDao.existsByEmail(email.trim().toLowerCase());
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        return customerDao.existsByPhoneNumber(phoneNumber.trim());
    }

    @Override
    public boolean existsByEmailAndNotCustomerId(String email, Long excludeCustomerId) {
        if (email == null || email.trim().isEmpty() || excludeCustomerId == null) {
            return false;
        }
        return customerDao.existsByEmailAndNotCustomerId(email.trim().toLowerCase(), excludeCustomerId);
    }

    @Override
    public boolean existsByPhoneNumberAndNotCustomerId(String phoneNumber, Long excludeCustomerId) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty() || excludeCustomerId == null) {
            return false;
        }
        return customerDao.existsByPhoneNumberAndNotCustomerId(phoneNumber.trim(), excludeCustomerId);
    }

    @Override
    public long getCustomerCountByMonth(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Tháng phải từ 1 đến 12");
        }
        if (year < 1900 || year > LocalDate.now().getYear() + 1) {
            throw new IllegalArgumentException("Năm không hợp lệ");
        }
        return customerDao.countByCreatedAtYearAndMonth(year, month);
    }

    @Override
    public byte[] exportCustomersToCSV() {
        List<Customer> customers = getAllCustomers();
        StringBuilder csv = new StringBuilder();
        
        csv.append("ID,Họ,Tên,Email,Số điện thoại,Địa chỉ,Ngày sinh,Giới tính,Trạng thái,Ghi chú,Ngày tạo,Ngày cập nhật\n");
        
        for (Customer customer : customers) {
            csv.append(customer.getId()).append(",")
               .append(escapeCsv(customer.getLastName())).append(",")
               .append(escapeCsv(customer.getFirstName())).append(",")
               .append(escapeCsv(customer.getEmail())).append(",")
               .append(escapeCsv(customer.getPhoneNumber())).append(",")
               .append(escapeCsv(customer.getAddress())).append(",")
               .append(customer.getDateOfBirth() != null ? customer.getDateOfBirth().toString() : "").append(",")
               .append(customer.getGender() != null ? customer.getGender().name() : "").append(",")
               .append(customer.isActive() ? "Hoạt động" : "Không hoạt động").append(",")
               .append(escapeCsv(customer.getNotes())).append(",")
               .append(customer.getCreatedAt()).append(",")
               .append(customer.getUpdatedAt()).append("\n");
        }
        
        return csv.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    @Override
    public int importCustomersFromCSV(byte[] csvData) {
        String csvContent = new String(csvData, java.nio.charset.StandardCharsets.UTF_8);
        String[] lines = csvContent.split("\n");
        int importedCount = 0;
        
        for (int i = 1; i < lines.length; i++) {
            try {
                String[] fields = lines[i].split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
                
                if (fields.length >= 4) {
                    Customer customer = new Customer();
                    customer.setLastName(fields[0].replace("\"", "").trim());
                    customer.setFirstName(fields[1].replace("\"", "").trim());
                    customer.setEmail(fields[2].replace("\"", "").trim().toLowerCase());
                    customer.setPhoneNumber(fields[3].replace("\"", "").trim());
                    
                    if (fields.length > 4 && !fields[4].isEmpty()) {
                        customer.setAddress(fields[4].replace("\"", "").trim());
                    }
                    
                    if (fields.length > 5 && !fields[5].isEmpty()) {
                        customer.setDateOfBirth(LocalDate.parse(fields[5].replace("\"", "").trim()));
                    }
                    
                    if (fields.length > 6 && !fields[6].isEmpty()) {
                        customer.setGender(Customer.Gender.valueOf(fields[6].replace("\"", "").trim()));
                    }
                    
                    customer.setCreatedAt(LocalDateTime.now());
                    customer.setUpdatedAt(LocalDateTime.now());
                    customer.setActive(true);
                    
                    createCustomer(customer);
                    importedCount++;
                }
            } catch (DuplicateCustomerException e) {
                System.err.println("Lỗi import dòng " + (i + 1) + ": " + e.getMessage());
            }
        }
        return importedCount;
    }

    private void validateCustomer(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Thông tin khách hàng không được để trống");
        }
        
        if (customer.getFirstName() == null || customer.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên không được để trống");
        }
        
        if (customer.getLastName() == null || customer.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ không được để trống");
        }
        
        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        
        if (!isValidEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Định dạng email không hợp lệ");
        }
        
        if (customer.getPhoneNumber() == null || customer.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }
        
        if (!isValidPhoneNumber(customer.getPhoneNumber())) {
            throw new IllegalArgumentException("Định dạng số điện thoại không hợp lệ");
        }
        
        customer.setFirstName(customer.getFirstName().trim());
        customer.setLastName(customer.getLastName().trim());
        customer.setEmail(customer.getEmail().trim().toLowerCase());
        customer.setPhoneNumber(customer.getPhoneNumber().trim());
        
        if (customer.getAddress() != null) {
            customer.setAddress(customer.getAddress().trim());
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber != null && 
               phoneNumber.matches("^(\\+84|0)[3|5|7|8|9][0-9]{8}$");
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}