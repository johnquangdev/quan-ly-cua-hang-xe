/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.exception;

/**
 *
 * @author Minh
 */
public class SystemManagementFoundException extends Exception {
    public SystemManagementFoundException(String message) {
        super(message);
    }

    public static SystemManagementFoundException withId(Long id) {
        return new SystemManagementFoundException("Không tìm thấy SystemManagement với ID = " + id);
    }

    public static SystemManagementFoundException withName(String name) {
        return new SystemManagementFoundException("Không tìm thấy SystemManagement với tên = " + name);
    }
}