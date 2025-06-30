/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.service.testcase.user;

import com.quanlycuahangxe.model.User;
import com.quanlycuahangxe.service.impl.UserServiceImpl;
import com.quanlycuahangxe.service.interfaces.UserService;
import com.quanlycuahangxe.utils.ServiceResult;

/**
 *
 * @author gunnguyen
 */
public class TestRole {

    private static final UserService userService = new UserServiceImpl();

    public static void main(String[] args) {
        testInsufficientPermission();
        testInvalidRole();

    }

    private static void testInsufficientPermission() {
        System.err.println("-- test quyền tạo role");

        // Giả lập current user là STAFF
        User currentUser = new User("staff_user", "dummyHash", "Nhân viên A", "staff@example.com", 3, true); // 3 = role_id của STAFF

        // Thử tạo một user với role MANAGER (quyền cao hơn)
        ServiceResult<User> result = userService.createUser(
                currentUser,
                "test_manager",
                "password123",
                "Quản lý B",
                "manager_test@example.com",
                "MANAGER",
                "Cửa hàng A",
                "0988000011"
        );

        if (!result.isSuccess()) {
            System.out.println(" test ok: Không đủ quyền tạo: " + result.getMessage());
        } else {
            System.out.println(" test fail: STAFF vẫn tạo được MANAGER");
        }
    }

    private static void testInvalidRole() {
        System.err.println("-- test tạo role không tồn tại");
        ServiceResult<User> result = userService.createUser(
                "testuser_invalid", "pass000", "Test Error", "testinvalid@gmail.com", "1", "", ""
        );
        if (!result.isSuccess()) {
            System.out.println("test ok: " + result.getMessage());
        } else {
            System.out.println("test fail: " + result.getMessage());
        }
    }
}
