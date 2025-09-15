/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.exception;

/**
 *
 * @author Minh
 */
public class DuplicateSystemManagementException extends Exception {
    public DuplicateSystemManagementException(String message) {
        super(message);
    }

    public static DuplicateSystemManagementException duplicateName(String name) {
        return new DuplicateSystemManagementException("SystemManagement với tên '" + name + "' đã tồn tại.");
    }
}