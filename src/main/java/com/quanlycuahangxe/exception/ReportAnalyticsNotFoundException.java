/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.exception;

/**
 *
 * @author Minh
 */
public class ReportAnalyticsNotFoundException extends Exception {
    public ReportAnalyticsNotFoundException() {
        super("Báo cáo không tồn tại");
    }
    
    public ReportAnalyticsNotFoundException(String message) {
        super(message);
    }
    
    public ReportAnalyticsNotFoundException(int reportId) {
        super("Báo cáo với ID " + reportId + " không tồn tại");
    }
}