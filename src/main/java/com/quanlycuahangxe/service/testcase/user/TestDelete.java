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
public class TestDelete {

    private static final UserService userService = new UserServiceImpl();

    public static void main(String[] args) {
        testDeleteUser();
    }
//1. test xoá user nếu đúng id xoá nếu sai id không tìm thấy id

    private static void testDeleteUser() {
        System.err.println("-- test xoá user");
        int userId = 3; // ID user cần xóa
        ServiceResult<Void> result = userService.deleteUser(userId);
        if (result.isSuccess()) {
            System.out.println("Test OK - Deleted user ID: " + userId);
        } else {
            System.err.println("Test FAIL - " + result.getMessage());
        }
    }
}
