/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.quanlycuahangxe.service.interfaces;

import com.quanlycuahangxe.model.Customer;
import com.quanlycuahangxe.exception.CustomerNotFoundException;
import com.quanlycuahangxe.exception.DuplicateCustomerException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author Minh
 */
public interface CustomerManagement {
    Customer createCustomer(Customer customer) throws DuplicateCustomerException;
    Customer updateCustomer(Long customerId, Customer customerDetails) throws CustomerNotFoundException, DuplicateCustomerException;

    Optional<Customer> findCustomerById(Long customerId);
    Customer getCustomerById(Long customerId) throws CustomerNotFoundException;

    Optional<Customer> findCustomerByEmail(String email);
    Optional<Customer> findCustomerByPhoneNumber(String phoneNumber);

    List<Customer> getAllCustomers();
    List<Customer> getCustomersWithPaging(int page, int size);
    List<Customer> getActiveCustomers();
    List<Customer> getInactiveCustomers();
    List<Customer> searchCustomers(String keyword);
    List<Customer> findCustomersByName(String firstName, String lastName);
    List<Customer> findCustomersByBirthDateRange(LocalDate startDate, LocalDate endDate);

    void deactivateCustomer(Long customerId) throws CustomerNotFoundException;
    void activateCustomer(Long customerId) throws CustomerNotFoundException;
    void deleteCustomer(Long customerId) throws CustomerNotFoundException;
    int deleteMultipleCustomers(List<Long> customerIds);

    long countCustomers();
    long countActiveCustomers();
    long countInactiveCustomers();

    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    boolean existsByEmailAndNotCustomerId(String email, Long excludeCustomerId);
    boolean existsByPhoneNumberAndNotCustomerId(String phoneNumber, Long excludeCustomerId);

    long getCustomerCountByMonth(int year, int month);

    byte[] exportCustomersToCSV();
    int importCustomersFromCSV(byte[] csvData);
}
