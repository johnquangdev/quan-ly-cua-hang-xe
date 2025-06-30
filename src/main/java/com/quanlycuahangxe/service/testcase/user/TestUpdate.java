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
public class TestUpdate {

    private static final UserService userService = new UserServiceImpl();

    public static void main(String[] args) {
        testUpdateUser();
    }

    private static void testUpdateUser() {
        System.err.println("-- test update user");
        int userId = 1;
        String fullName = "john";
        String email = "newemail@example.com";
        String roleName = "MANAGER";

        ServiceResult<User> result = userService.updateUser(userId, fullName, email, roleName);
        if (result.isSuccess()) {
            System.out.println("Test OK - Updated user: " + result.getData().getFullName());
        } else {
            System.err.println("Test FAIL - " + result.getMessage());
        }
    }
}
