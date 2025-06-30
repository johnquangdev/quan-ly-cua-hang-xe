/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.quanlycuahangxe.service.interfaces;

import com.quanlycuahangxe.model.User;
import com.quanlycuahangxe.utils.ServiceResult;
import java.util.List;

/**
 *
 * @author gunnguyen
 */
public interface UserService {

    // Tạo user mới với check role
    ServiceResult<User> createUser(User currentUser, String username, String password, String fullName,
            String email, String roleName, String position, String phone);

    // Tạo user không check role
    ServiceResult<User> createUser(String username, String password, String fullName,
            String email, String roleName, String position, String phone);

    // Lấy user theo ID
    ServiceResult<User> getUserById(int id);

    // Lấy user theo username
    ServiceResult<User> getUserByUsername(String username);

    // Lấy tất cả users
    ServiceResult<List<User>> getAllUsers();

    // Xác thực đăng nhập
    ServiceResult<User> authenticateUser(String username, String password);

    // Logout
    ServiceResult<Void> logout(User user);

    // Khóa/mở khóa user
    ServiceResult<Void> toggleUserLock(int userId);

    // Cập nhật user
    ServiceResult<User> updateUser(int id, String fullName, String email, String roleName);

    // Xóa user
    ServiceResult<Void> deleteUser(int userId);

    // Đổi password
    ServiceResult<Void> changePassword(int userId, String oldPassword, String newPassword);
}
