/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.service.testcase.user;

import com.quanlycuahangxe.model.User;
import com.quanlycuahangxe.service.impl.UserServiceImpl;
import com.quanlycuahangxe.service.interfaces.UserService;
import com.quanlycuahangxe.utils.ServiceResult;
import java.util.List;

/**
 *
 * @author gunnguyen
 */
public class TestGetAll {

    private static final UserService userService = new UserServiceImpl();

    public static void main(String[] args) {
        testGetAllUsers();
    }

    private static void testGetAllUsers() {
        System.err.println("-- test get all user");
        ServiceResult<List<User>> result = userService.getAllUsers();
        if (result.isSuccess()) {
            System.out.println("Test OK - Total users: " + result.getData().size());
        } else {
            System.err.println("Test FAIL - " + result.getMessage());
        }
    }
}
