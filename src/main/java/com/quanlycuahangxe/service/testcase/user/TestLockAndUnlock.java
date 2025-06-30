/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.service.testcase.user;

import com.quanlycuahangxe.service.impl.UserServiceImpl;
import com.quanlycuahangxe.service.interfaces.UserService;
import com.quanlycuahangxe.utils.ServiceResult;

/**
 *
 * @author gunnguyen
 */
public class TestLockAndUnlock {

    private static final UserService userService = new UserServiceImpl();

    public static void main(String[] args) {
        testToggleUserLock();
    }

    private static void testToggleUserLock() {
        System.err.println("-- test lock and unlock");
        int userId = 2; // ID của user cần khóa hoặc mở
        ServiceResult<Void> result = userService.toggleUserLock(userId);
        if (result.isSuccess()) {
            System.out.println("Test OK - Toggle lock successful");
        } else {
            System.err.println("Test FAIL - " + result.getMessage());
        }
    }
}
