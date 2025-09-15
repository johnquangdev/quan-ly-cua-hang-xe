/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.service.impl;

import com.quanlycuahangxe.service.interfaces.InventoryManagement;
import com.quanlycuahangxe.dao.InventoryManagementDao;
import com.quanlycuahangxe.model.Inventory;
import com.quanlycuahangxe.exception.InventoryNotFoundException;
import com.quanlycuahangxe.exception.DuplicateInventoryException;
import com.quanlycuahangxe.exception.DuplicateReportAnalyticsException;
import com.quanlycuahangxe.exception.ReportAnalyticsNotFoundException;
import com.quanlycuahangxe.model.ReportAnalytics;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Minh
 */
public class InventoryManagementImpl implements InventoryManagement {
    private final InventoryManagementDao inventoryDao;
    
    public InventoryManagementImpl() {
        this.inventoryDao = new InventoryManagementDao();
    }
    
    public boolean addInventory(Inventory inventory) throws DuplicateInventoryException {
        try {
            Inventory createdInventory = inventoryDao.createInventory(inventory);
            return createdInventory != null;
        } catch (DuplicateInventoryException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thêm inventory record: " + e.getMessage(), e);
        }
    }
    
    public Inventory getInventoryById(int inventoryId) throws InventoryNotFoundException {
        try {
            return inventoryDao.getInventoryById(inventoryId);
        } catch (InventoryNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy inventory record: " + e.getMessage(), e);
        }
    }
    
    public Inventory getInventoryByCarId(int carId) throws InventoryNotFoundException {
        try {
            return inventoryDao.getInventoryByCarId(carId);
        } catch (InventoryNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy inventory record theo car ID: " + e.getMessage(), e);
        }
    }
    
    public List<Inventory> getAllInventory() {
        try {
            return inventoryDao.getAllInventory();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách inventory: " + e.getMessage(), e);
        }
    }
    
    public boolean updateInventory(Inventory inventory) throws InventoryNotFoundException {
        try {
            Inventory updatedInventory = inventoryDao.updateInventory(inventory.getInventoryId(), inventory);
            return updatedInventory != null;
        } catch (InventoryNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật inventory record: " + e.getMessage(), e);
        }
    }
    
    public boolean deleteInventory(int inventoryId) throws InventoryNotFoundException {
        try {
            inventoryDao.deleteInventory(inventoryId);
            return true;
        } catch (InventoryNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa inventory record: " + e.getMessage(), e);
        }
    }
    
    public Inventory updateStockQuantity(int carId, int quantityChange) throws InventoryNotFoundException {
        try {
            return inventoryDao.updateStockQuantity(carId, quantityChange);
        } catch (InventoryNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật số lượng tồn kho: " + e.getMessage(), e);
        }
    }
    
    public boolean checkStockAvailability(int carId, int requiredQuantity) {
        try {
            return inventoryDao.checkStockAvailability(carId, requiredQuantity);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi kiểm tra số lượng tồn kho: " + e.getMessage(), e);
        }
    }
    
    public List<Inventory> getInventoryByStatus(String status) {
        try {
            return inventoryDao.getInventoryByStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy inventory theo status: " + e.getMessage(), e);
        }
    }
    
    public List<Inventory> getLowStockInventory() {
        try {
            return inventoryDao.getLowStockInventory();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy inventory số lượng thấp: " + e.getMessage(), e);
        }
    }
    
    public List<Inventory> getOutOfStockInventory() {
        try {
            return inventoryDao.getOutOfStockInventory();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy inventory hết hàng: " + e.getMessage(), e);
        }
    }
    
    public List<Inventory> getInventoryNeedRestock() {
        try {
            return inventoryDao.getInventoryNeedRestock();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy inventory cần restock: " + e.getMessage(), e);
        }
    }
    
    public List<Inventory> searchInventoryByKeyword(String keyword) {
        try {
            return inventoryDao.searchInventoryByKeyword(keyword);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tìm kiếm inventory: " + e.getMessage(), e);
        }
    }
    
    public int getTotalInventoryQuantity() {
        try {
            return inventoryDao.getTotalInventoryQuantity();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tính tổng số lượng tồn kho: " + e.getMessage(), e);
        }
    }
    
    public double getTotalInventoryValue() {
        try {
            return inventoryDao.getTotalInventoryValue();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tính tổng giá trị tồn kho: " + e.getMessage(), e);
        }
    }
    
    public Map<String, Integer> getInventoryStatusStats() {
        try {
            return inventoryDao.getInventoryStatusStats();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy thống kê inventory status: " + e.getMessage(), e);
        }
    }
    
    public List<Inventory> getInventoryHistory(int carId, LocalDate startDate, LocalDate endDate) {
        try {
            return inventoryDao.getInventoryHistory(carId, startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy lịch sử inventory: " + e.getMessage(), e);
        }
    }
    
    public boolean existsByCarId(int carId) {
        try {
            return inventoryDao.existsByCarId(carId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi kiểm tra inventory existence: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean generateSalesReport(LocalDateTime startDate, LocalDateTime endDate, String generatedBy) 
            throws DuplicateReportAnalyticsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean generateInventoryReport(String generatedBy) 
            throws DuplicateReportAnalyticsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean generateCustomerReport(LocalDateTime startDate, LocalDateTime endDate, String generatedBy) 
            throws DuplicateReportAnalyticsException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ReportAnalytics getReportById(int reportId) throws ReportAnalyticsNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ReportAnalytics> getAllReports() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ReportAnalytics> getReportsByType(String reportType) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public List<ReportAnalytics> getReportsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public boolean deleteReport(int reportId) throws ReportAnalyticsNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String exportReportToJson(int reportId) throws ReportAnalyticsNotFoundException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}