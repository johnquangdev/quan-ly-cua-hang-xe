/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.quanlycuahangxe.service.interfaces;

/**
 *
 * @author gunnguyen
 */
import com.quanlycuahangxe.model.Category;
import com.quanlycuahangxe.utils.ServiceResult;
import java.util.List;

public interface CategoryService {

    ServiceResult<Category> createCategory(String name, String description);

    ServiceResult<Category> getCategoryById(int id);

    ServiceResult<List<Category>> getAllCategories();

    ServiceResult<Category> updateCategory(int id, String name, String description);

    ServiceResult<Void> deleteCategory(int id);
}
