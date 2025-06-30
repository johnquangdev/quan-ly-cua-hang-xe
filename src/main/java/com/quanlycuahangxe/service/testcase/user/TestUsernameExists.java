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
public class TestUsernameExists {

    private static final UserService userService = new UserServiceImpl();

    public static void main(String[] args) {
        testUsernameExists();
    }

    private static void testUsernameExists() {
        System.err.println("-- test user tồn tại không");
        ServiceResult<User> result = userService.getUserByUsername("john");
        if (result.isSuccess()) {
            System.out.println("Đúng: user name tồn tại: ");
        } else {
            System.out.println("Sai: Username không tồn tại");
        }
    }
}
