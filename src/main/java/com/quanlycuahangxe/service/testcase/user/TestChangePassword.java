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
public class TestChangePassword {

    private static final UserService userService = new UserServiceImpl();

    public static void main(String[] args) {
        changePassword_Succces();
//        changePassword_HashPasswordError();
        changePassword_UserNotFound();
        changePassword_TooShortNewPassword();
        changePassword_InvalidOldPassword();
    }

//1. test ok: Đổi mật khẩu thành công
    private static void changePassword_Succces() {
        System.err.println("-- test ChangePassword");
        int userId = 4;
        String oldPassword = "123456";
        String newPassword = "newpass123";

        ServiceResult<Void> result = userService.changePassword(userId, oldPassword, newPassword);
        if (result.isSuccess()) {
            System.out.println("Test OK - Password changed");
        } else {
            System.err.println("Test FAIL - " + result.getMessage());
        }
    }

//2. test ok: Mật khẩu cũ không đúng
    private static void changePassword_InvalidOldPassword() {
        System.err.println("-- test Invalid Old Password");
        int userId = 3;
        String oldPassword = "1234";
        String newPassword = "newpass123";

        ServiceResult<Void> result = userService.changePassword(userId, oldPassword, newPassword);
        if (result.isSuccess()) {
            System.out.println("Test OK - Password changed");
        } else {
            System.err.println("Test FAIL - " + result.getMessage());
        }
    }
//3. test ok: Mật khẩu mới phải có ít nhất 6 ký tự

    private static void changePassword_TooShortNewPassword() {
        System.err.println("-- test Too Short New Password");
        int userId = 3;
        String oldPassword = "123456";
        String newPassword = "new";

        ServiceResult<Void> result = userService.changePassword(userId, oldPassword, newPassword);
        if (result.isSuccess()) {
            System.out.println("Test OK - Password changed");
        } else {
            System.err.println("Test FAIL - " + result.getMessage());
        }
    }
//4. test ok: Không tìm thấy user

    private static void changePassword_UserNotFound() {
        System.err.println("-- test User Not Found");
        int userId = 10;
        String oldPassword = "123456";
        String newPassword = "newpass123";

        ServiceResult<Void> result = userService.changePassword(userId, oldPassword, newPassword);
        if (result.isSuccess()) {
            System.out.println("Test OK - Password changed");
        } else {
            System.err.println("Test FAIL - " + result.getMessage());
        }
    }
//5. test ok: Lỗi mã hóa mật khẩu mới

//    private static void changePassword_HashPasswordError() {
//        System.err.println("-- test Hash Password");
//    }
}
