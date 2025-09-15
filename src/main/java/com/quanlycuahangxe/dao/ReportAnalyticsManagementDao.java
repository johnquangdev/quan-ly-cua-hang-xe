/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.dao;

import com.quanlycuahangxe.db.NewConnectPostgres;
import com.quanlycuahangxe.model.ReportAnalytics;
import com.quanlycuahangxe.exception.ReportAnalyticsNotFoundException;
import com.quanlycuahangxe.exception.DuplicateReportAnalyticsException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Minh
 */
public class ReportAnalyticsManagementDao {
    private final Connection connection;

    public ReportAnalyticsManagementDao() {
        this.connection = NewConnectPostgres.getConnection();
    }

    public boolean createReport(ReportAnalytics report) throws DuplicateReportAnalyticsException {
        String sql = "INSERT INTO reports_analytics (report_type, start_date, end_date, data, generated_by, generated_at) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, report.getReportType());
            stmt.setTimestamp(2, report.getStartDate() != null ? Timestamp.valueOf(report.getStartDate()) : null);
            stmt.setTimestamp(3, report.getEndDate() != null ? Timestamp.valueOf(report.getEndDate()) : null);
            stmt.setString(4, report.getData());
            stmt.setString(5, report.getGeneratedBy());
            stmt.setTimestamp(6, Timestamp.valueOf(report.getGeneratedAt()));

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    report.setReportId(generatedKeys.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new DuplicateReportAnalyticsException("Báo cáo đã tồn tại: " + e.getMessage());
            }
            throw new DuplicateReportAnalyticsException("Lỗi khi tạo báo cáo: " + e.getMessage());
        }
    }

    public ReportAnalytics getReportById(int reportId) throws ReportAnalyticsNotFoundException {
        String sql = "SELECT * FROM reports_analytics WHERE report_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reportId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToReport(rs);
            }
            throw new ReportAnalyticsNotFoundException(reportId);

        } catch (SQLException e) {
            throw new ReportAnalyticsNotFoundException("Lỗi khi lấy báo cáo: " + e.getMessage());
        }
    }

    public List<ReportAnalytics> getAllReports() {
        List<ReportAnalytics> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports_analytics ORDER BY generated_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
            return reports;

        } catch (SQLException e) {
            e.printStackTrace();
            return reports;
        }
    }

    public List<ReportAnalytics> getReportsByType(String reportType) {
        List<ReportAnalytics> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports_analytics WHERE report_type = ? ORDER BY generated_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, reportType);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
            return reports;

        } catch (SQLException e) {
            e.printStackTrace();
            return reports;
        }
    }

    public List<ReportAnalytics> getReportsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<ReportAnalytics> reports = new ArrayList<>();
        String sql = "SELECT * FROM reports_analytics WHERE generated_at BETWEEN ? AND ? ORDER BY generated_at DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                reports.add(mapResultSetToReport(rs));
            }
            return reports;

        } catch (SQLException e) {
            e.printStackTrace();
            return reports;
        }
    }

    public boolean deleteReport(int reportId) throws ReportAnalyticsNotFoundException {
        String sql = "DELETE FROM reports_analytics WHERE report_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reportId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new ReportAnalyticsNotFoundException(reportId);
            }
            return true;

        } catch (SQLException e) {
            throw new ReportAnalyticsNotFoundException("Lỗi khi xóa báo cáo: " + e.getMessage());
        }
    }

    public boolean existsById(int reportId) {
        String sql = "SELECT 1 FROM reports_analytics WHERE report_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, reportId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateReport(ReportAnalytics report) throws ReportAnalyticsNotFoundException, DuplicateReportAnalyticsException {
        String sql = "UPDATE reports_analytics SET report_type = ?, start_date = ?, end_date = ?, data = ?, generated_by = ?, generated_at = ? WHERE report_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, report.getReportType());
            stmt.setTimestamp(2, report.getStartDate() != null ? Timestamp.valueOf(report.getStartDate()) : null);
            stmt.setTimestamp(3, report.getEndDate() != null ? Timestamp.valueOf(report.getEndDate()) : null);
            stmt.setString(4, report.getData());
            stmt.setString(5, report.getGeneratedBy());
            stmt.setTimestamp(6, Timestamp.valueOf(report.getGeneratedAt()));
            stmt.setInt(7, report.getReportId());

            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new ReportAnalyticsNotFoundException(report.getReportId());
            }
            return true;

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new DuplicateReportAnalyticsException("Báo cáo đã tồn tại: " + e.getMessage());
            }
            throw new DuplicateReportAnalyticsException("Lỗi khi cập nhật báo cáo: " + e.getMessage());
        }
    }

    private ReportAnalytics mapResultSetToReport(ResultSet rs) throws SQLException {
        ReportAnalytics report = new ReportAnalytics();
        report.setReportId(rs.getInt("report_id"));
        report.setReportType(rs.getString("report_type"));
        
        Timestamp startDate = rs.getTimestamp("start_date");
        if (startDate != null) {
            report.setStartDate(startDate.toLocalDateTime());
        }
        
        Timestamp endDate = rs.getTimestamp("end_date");
        if (endDate != null) {
            report.setEndDate(endDate.toLocalDateTime());
        }
        
        report.setData(rs.getString("data"));
        report.setGeneratedBy(rs.getString("generated_by"));
        report.setGeneratedAt(rs.getTimestamp("generated_at").toLocalDateTime());
        
        return report;
    }
}