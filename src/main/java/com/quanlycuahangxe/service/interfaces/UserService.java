/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this
 * license Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this
 * template
 */
package com.quanlycuahangxe.service.interfaces;

import java.util.List;

import com.quanlycuahangxe.model.User;
import com.quanlycuahangxe.utils.ServiceResult;

/**
 *
 * @author gunnguyen
 */
public interface UserService {

    ServiceResult<User> createUser(User currentUser, String username, String password,
    String fullName, String email, String roleName, String position, String phone);
    ServiceResult<User> getUserById(int id);

    ServiceResult<User> getUserByUsername(String username);

    ServiceResult<List<User>> getAllUsers();

    ServiceResult<User> authenticateUser(String username, String password);

    ServiceResult<Void> logout(User user);

    ServiceResult<Void> toggleUserLock(int userId);

    ServiceResult<User> updateUser(int id, String fullName, String email, String roleName);
    
    ServiceResult<Void> deleteUser(int userId);
    
    ServiceResult<Void> changePassword(int userId, String oldPassword, String newPassword);
    
    ServiceResult<List<User>> searchUsers(String keyword);
}
