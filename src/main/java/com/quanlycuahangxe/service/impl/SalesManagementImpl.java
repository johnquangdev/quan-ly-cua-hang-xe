/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.service.impl;

import com.quanlycuahangxe.service.interfaces.SalesManagement;
import com.quanlycuahangxe.dao.SalesManagementDao;
import com.quanlycuahangxe.model.Sales;
import com.quanlycuahangxe.exception.SalesNotFoundException;
import com.quanlycuahangxe.exception.DuplicateSalesException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;


/**
 *
 * @author Minh
 */
public class SalesManagementImpl implements SalesManagement {
    private final SalesManagementDao salesDao;
    
    public SalesManagementImpl() {
        this.salesDao = new SalesManagementDao();
    }
    
    @Override
    public boolean addSale(Sales sale) throws DuplicateSalesException {
        try {
            Sales createdSale = salesDao.createSale(sale);
            return createdSale != null;
        } catch (Exception e) {
            if (e instanceof DuplicateSalesException) {
                throw (DuplicateSalesException) e;
            }
            throw new RuntimeException("Lỗi khi thêm giao dịch bán hàng: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Sales getSaleById(int saleId) throws SalesNotFoundException {
        try {
            return salesDao.getSaleById(saleId);
        } catch (SalesNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy giao dịch bán hàng: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Sales> getAllSales() {
        try {
            return salesDao.getAllSales();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách giao dịch bán hàng: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean updateSale(Sales sale) throws SalesNotFoundException {
        try {
            Sales updatedSale = salesDao.updateSale(sale.getSaleId(), sale);
            return updatedSale != null;
        } catch (SalesNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật giao dịch bán hàng: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean deleteSale(int saleId) throws SalesNotFoundException {
        try {
            salesDao.deleteSale(saleId);
            return true;
        } catch (SalesNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa giao dịch bán hàng: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Sales> getSalesByCustomer(int customerId) {
        try {
            return salesDao.findByCustomerId(customerId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy giao dịch theo khách hàng: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Sales> getSalesByEmployee(int employeeId) {
        try {
            return salesDao.findByEmployeeId(employeeId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy giao dịch theo nhân viên: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Sales> getSalesByCar(int carId) {
        try {
            return salesDao.findByCarId(carId);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy giao dịch theo xe: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Sales> getSalesByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            return salesDao.findByDateRange(startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy giao dịch theo khoảng thời gian: " + e.getMessage(), e);
        }
    }
    
    @Override
    public double getTotalRevenueByDateRange(LocalDate startDate, LocalDate endDate) {
        try {
            return salesDao.getTotalRevenueByDateRange(startDate, endDate);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tính tổng doanh thu: " + e.getMessage(), e);
        }
    }
    
    @Override
    public int getSalesCountByStatus(String status) {
        try {
            return (int) salesDao.countByStatus(status);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đếm giao dịch theo trạng thái: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<String, Double> getMonthlyRevenue(int year) {
        try {
            return salesDao.getMonthlyRevenue(year);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tính doanh thu theo tháng: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<Integer, Double> getTopSalesEmployees(int limit) {
        try {
            return salesDao.getTopSalesEmployees(limit);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy top nhân viên bán hàng: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Map<Integer, Integer> getTopSellingCars(int limit) {
        try {
            return salesDao.getTopSellingCars(limit);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy top xe bán chạy: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean checkCarAvailability(int carId, int quantity) {
        try {
            return salesDao.checkCarAvailability(carId, quantity);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi kiểm tra số lượng xe: " + e.getMessage(), e);
        }
    }
    
    @Override
    public double getTodayRevenue() {
        LocalDate today = LocalDate.now();
        return getTotalRevenueByDateRange(today, today);
    }
    
    @Override
    public int getTodaySalesCount() {
        LocalDate today = LocalDate.now();
        List<Sales> todaySales = getSalesByDateRange(today, today);
        return todaySales.size();
    }
    
    @Override
    public double getThisMonthRevenue() {
        LocalDate now = LocalDate.now();
        LocalDate startOfMonth = LocalDate.of(now.getYear(), now.getMonth(), 1);
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);
        return getTotalRevenueByDateRange(startOfMonth, endOfMonth);
    }
    
    @Override
    public double getThisYearRevenue() {
        LocalDate now = LocalDate.now();
        LocalDate startOfYear = LocalDate.of(now.getYear(), 1, 1);
        LocalDate endOfYear = LocalDate.of(now.getYear(), 12, 31);
        return getTotalRevenueByDateRange(startOfYear, endOfYear);
    }
}