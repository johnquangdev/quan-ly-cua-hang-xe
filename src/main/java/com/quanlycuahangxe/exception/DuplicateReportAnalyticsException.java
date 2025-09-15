/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.exception;

/**
 *
 * @author Minh
 */
public class DuplicateReportAnalyticsException extends Exception {
    public DuplicateReportAnalyticsException() {
        super("Báo cáo đã tồn tại");
    }
    
    public DuplicateReportAnalyticsException(String message) {
        super(message);
    }
    
    public DuplicateReportAnalyticsException(int reportId) {
        super("Báo cáo với ID " + reportId + " đã tồn tại");
    }
}