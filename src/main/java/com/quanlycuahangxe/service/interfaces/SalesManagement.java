/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.quanlycuahangxe.service.interfaces;

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
public interface SalesManagement {
    boolean addSale(Sales sale) throws DuplicateSalesException;

    Sales getSaleById(int saleId) throws SalesNotFoundException;

    List<Sales> getAllSales();

    boolean updateSale(Sales sale) throws SalesNotFoundException;

    boolean deleteSale(int saleId) throws SalesNotFoundException;

    List<Sales> getSalesByCustomer(int customerId);

    List<Sales> getSalesByEmployee(int employeeId);

    List<Sales> getSalesByCar(int carId);

    List<Sales> getSalesByDateRange(LocalDate startDate, LocalDate endDate);

    double getTotalRevenueByDateRange(LocalDate startDate, LocalDate endDate);

    int getSalesCountByStatus(String status);

    Map<String, Double> getMonthlyRevenue(int year);

    Map<Integer, Double> getTopSalesEmployees(int limit);

    Map<Integer, Integer> getTopSellingCars(int limit);

    boolean checkCarAvailability(int carId, int quantity);

    double getTodayRevenue();

    int getTodaySalesCount();

    double getThisMonthRevenue();

    double getThisYearRevenue();
}