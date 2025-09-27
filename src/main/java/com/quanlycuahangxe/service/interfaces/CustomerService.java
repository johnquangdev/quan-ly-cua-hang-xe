/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.quanlycuahangxe.service.interfaces;

/**
 *
 * @author gunnguyen
 */
import com.quanlycuahangxe.model.Customer;
import com.quanlycuahangxe.utils.ServiceResult;
import java.util.List;

public interface CustomerService {

    ServiceResult<Customer> createCustomer(String fullName, String email, String phone, String address);

    ServiceResult<Customer> getCustomerById(int id);

    ServiceResult<List<Customer>> getAllCustomers();

    ServiceResult<Customer> updateCustomer(int id, String fullName, String email, String phone, String address);

    ServiceResult<Void> deleteCustomer(int id);
    
    ServiceResult<List<Customer>> searchCustomers(String keyword);
}
