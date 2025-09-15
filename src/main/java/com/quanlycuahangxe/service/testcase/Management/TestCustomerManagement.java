/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.service.testcase.Management;

import com.quanlycuahangxe.model.Customer;
import com.quanlycuahangxe.service.impl.CustomerManagementImpl;
import com.quanlycuahangxe.exception.CustomerNotFoundException;
import com.quanlycuahangxe.exception.DuplicateCustomerException;

import java.time.LocalDate;
import java.util.List;

/**
 *
 * @author Minh
 */
public class TestCustomerManagement {
    public static void main(String[] args) {
        CustomerManagementImpl customerService = new CustomerManagementImpl();

        try {
            // 1. Tạo khách hàng mới
            Customer c1 = new Customer();
            c1.setFirstName("Minh");
            c1.setLastName("Tran");
            c1.setEmail("minh@example.com");
            c1.setPhoneNumber("0912345678");
            c1.setDateOfBirth(LocalDate.of(2000, 1, 1));
            c1.setAddress("Hà Nội");

            Customer saved1 = customerService.createCustomer(c1);
            System.out.println("✅ Tạo khách hàng thành công: " + saved1.getId());

            // 2. Tạo thêm khách hàng khác
            Customer c2 = new Customer();
            c2.setFirstName("Hương");
            c2.setLastName("Nguyen");
            c2.setEmail("huong@example.com");
            c2.setPhoneNumber("0987654321");
            c2.setDateOfBirth(LocalDate.of(1995, 5, 20));
            c2.setAddress("TP.HCM");

            Customer saved2 = customerService.createCustomer(c2);
            System.out.println("✅ Tạo khách hàng thành công: " + saved2.getId());

            // 3. Lấy danh sách tất cả khách hàng
            List<Customer> all = customerService.getAllCustomers();
            System.out.println("\n📋 Danh sách khách hàng:");
            for (Customer c : all) {
                System.out.println("- " + c.getId() + " | " + c.getLastName() + " " + c.getFirstName()
                        + " | " + c.getEmail() + " | " + c.getPhoneNumber());
            }

            // 4. Tìm khách hàng theo email
            customerService.findCustomerByEmail("minh@example.com")
                    .ifPresent(c -> System.out.println("\n🔎 Tìm thấy khách hàng theo email: " + c.getFirstName()));

            // 5. Cập nhật khách hàng
            c1.setAddress("Hà Nội - Ba Đình");
            Customer updated = customerService.updateCustomer(saved1.getId(), c1);
            System.out.println("✏️  Đã cập nhật địa chỉ: " + updated.getAddress());

            // 6. Đếm số lượng khách hàng
            long count = customerService.countCustomers();
            System.out.println("\n📊 Tổng số khách hàng: " + count);

            // 7. Xóa khách hàng
            customerService.deleteCustomer(saved2.getId());
            System.out.println("🗑️  Đã xóa khách hàng ID: " + saved2.getId());

        } catch (DuplicateCustomerException e) {
            System.err.println("❌ Lỗi trùng lặp: " + e.getMessage());
        } catch (CustomerNotFoundException e) {
            System.err.println("❌ Không tìm thấy khách hàng: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}