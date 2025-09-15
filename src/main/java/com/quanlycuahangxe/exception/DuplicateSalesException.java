/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.exception;

/**
 *
 * @author Minh
 */
public class DuplicateSalesException extends Exception {
    private static final long serialVersionUID = 1L;
    
    public DuplicateSalesException() {
        super("Duplicate sales record");
    }
    
    public DuplicateSalesException(String message) {
        super(message);
    }
    
    public DuplicateSalesException(int saleId) {
        super("Sales record with ID " + saleId + " already exists");
    }
    
    public static DuplicateSalesException duplicateSale(int saleId) {
        return new DuplicateSalesException("Sales record with ID " + saleId + " already exists");
    }
}