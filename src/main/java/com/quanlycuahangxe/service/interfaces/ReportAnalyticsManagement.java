/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.quanlycuahangxe.service.interfaces;

import com.quanlycuahangxe.model.ReportAnalytics;
import com.quanlycuahangxe.exception.ReportAnalyticsNotFoundException;
import com.quanlycuahangxe.exception.DuplicateReportAnalyticsException;
import java.time.LocalDateTime;
import java.util.List;

/**
 *
 * @author Minh
 */
public interface ReportAnalyticsManagement {
    boolean generateSalesReport(LocalDateTime startDate, LocalDateTime endDate, String generatedBy) throws DuplicateReportAnalyticsException;
    boolean generateInventoryReport(String generatedBy) throws DuplicateReportAnalyticsException;
    boolean generateCustomerReport(LocalDateTime startDate, LocalDateTime endDate, String generatedBy) throws DuplicateReportAnalyticsException;
    ReportAnalytics getReportById(int reportId) throws ReportAnalyticsNotFoundException;
    List<ReportAnalytics> getAllReports();
    List<ReportAnalytics> getReportsByType(String reportType);
    List<ReportAnalytics> getReportsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    boolean deleteReport(int reportId) throws ReportAnalyticsNotFoundException;
    String exportReportToJson(int reportId) throws ReportAnalyticsNotFoundException;
}