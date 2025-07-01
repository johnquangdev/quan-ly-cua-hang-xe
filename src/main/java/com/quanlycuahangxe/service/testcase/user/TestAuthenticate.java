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
public class TestAuthenticate {

    private static final UserService userService = new UserServiceImpl();

    public static void main(String[] args) {
        testAuthenticateUser_Login();
        testAuthenticateUser_Logout();
        testAuthenticateUser_LogoutSuccess();
    }

    //
    private static void testAuthenticateUser_Login() {
        System.err.println("-- test login");
        String username = "admin";
        String password = "123456";

        ServiceResult<User> result = userService.authenticateUser(username, password);
        if (result.isSuccess()) {
            System.out.println("Test OK - Login success: " + result.getData().getUsername());
        } else {
            System.err.println("Test FAIL - " + result.getMessage());
        }
    }

    private static void testAuthenticateUser_Logout() {
        System.err.println("-- test logout");
        User user = new User();
        ServiceResult<Void> result = userService.logout(user);
        if (result.isSuccess()) {
            System.out.println("Test OK - logout success ");
        } else {
            System.err.println("Test FAIL - " + result.getMessage());
        }
    }

    private static void testAuthenticateUser_LogoutSuccess() {
        System.err.println("-- test logout");
        User user = new User();
        user.setId(6);
        user.setUsername("testuser123");
        ServiceResult<Void> result = userService.logout(user);
        if (result.isSuccess()) {
            System.out.println("Test OK - logout success ");
        } else {
            System.err.println("Test FAIL - " + result.getMessage());
        }
    }
}
