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
public class TestGetUserById {

    private static final UserService userService = new UserServiceImpl();

    public static void main(String[] args) {
        testgetUserById();
    }

    private static void testgetUserById() {
        System.err.println("-- test get User By Id ");
        ServiceResult<User> result = userService.getUserById(444);
        if (result.isSuccess()) {
            System.out.println("test ok: " + result.getData());
        } else {
            System.err.println("test fail: " + result.getMessage());
        }
    }
}
