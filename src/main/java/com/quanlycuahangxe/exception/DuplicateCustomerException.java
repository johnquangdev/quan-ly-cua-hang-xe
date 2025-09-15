/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.exception;

/**
 *
 * @author Minh
 */
public class DuplicateCustomerException extends Exception {
    public DuplicateCustomerException(String msg) {
        super(msg);
    }

    public static DuplicateCustomerException duplicateEmail(String email) {
        return new DuplicateCustomerException("Email đã tồn tại: " + email);
    }

    public static DuplicateCustomerException duplicateEmail(String email, Long id) {
        return new DuplicateCustomerException("Email '" + email + "' đã tồn tại (thuộc về khách hàng ID=" + id + ")");
    }

    public static DuplicateCustomerException duplicatePhoneNumber(String phone) {
        return new DuplicateCustomerException("Số điện thoại đã tồn tại: " + phone);
    }

    public static DuplicateCustomerException duplicatePhoneNumber(String phone, Long id) {
        return new DuplicateCustomerException("Số điện thoại '" + phone + "' đã tồn tại (thuộc về khách hàng ID=" + id + ")");
    }
}