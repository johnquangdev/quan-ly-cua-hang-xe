/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.exception;

/**
 *
 * @author Minh
 */
public class SalesNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public SalesNotFoundException() {
        super("Sales record not found");
    }
    
    public SalesNotFoundException(String message) {
        super(message);
    }
    
    public SalesNotFoundException(int saleId) {
        super("Sales record with ID " + saleId + " not found");
    }
    
    public static SalesNotFoundException withId(int saleId) {
        return new SalesNotFoundException("Sales record with ID " + saleId + " not found");
    }
}