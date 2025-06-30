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
public class TestGetUserByUserName {

    private static final UserService userService = new UserServiceImpl();

    public static void main(String[] args) {
        testGetUserByUsername();
    }

    private static void testGetUserByUsername() {
        System.err.println("-- test get user by user name");
        String username = "admin";
        ServiceResult<User> result = userService.getUserByUsername(username);
        if (result.isSuccess()) {
            System.out.println("Test OK - Get user by username: " + result.getData().getFullName());
        } else {
            System.err.println("Test FAIL - " + result.getMessage());
        }
    }
}
