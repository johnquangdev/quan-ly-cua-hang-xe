/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this
 * license Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this
 * template
 */
package com.quanlycuahangxe.service.interfaces;

import java.sql.Connection;
import java.util.List;

import com.quanlycuahangxe.model.Product; // Import Connection class
import com.quanlycuahangxe.utils.ServiceResult;

/**
 *
 * @author gunnguyen
 */
public interface ProductService {

    ServiceResult<Product> createProduct(String name, int brandId, int categoryId, double price, int stockQty, String description);

    ServiceResult<Product> getProductById(int id);
    ServiceResult<Product> getProductById(Connection conn, int id);

    ServiceResult<List<Product>> getAllProducts();
    ServiceResult<List<Product>> getAllProducts(Connection conn);

    ServiceResult<Product> updateProduct(int id, String name, int brandId, int categoryId,double price, String description);
    ServiceResult<Product> updateProduct(Connection conn, int id, String name, int brandId,int categoryId, double price, String description);

    ServiceResult<Void> deleteProduct(int id);
    ServiceResult<Void> deleteProduct(Connection conn, int id);

    ServiceResult<Product> updateStock(int productId, int changeAmount);
    ServiceResult<Product> updateStock(Connection conn, int productId, int changeAmount);

    ServiceResult<List<Product>> searchProducts(String keyword);
}
