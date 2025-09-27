/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.quanlycuahangxe.service.interfaces;

/**
 *
 * @author gunnguyen
 */
import com.quanlycuahangxe.model.Brand;
import com.quanlycuahangxe.utils.ServiceResult;
import java.util.List;

public interface BrandService {

    ServiceResult<Brand> createBrand(String name, String description);

    ServiceResult<Brand> getBrandById(int id);

    ServiceResult<List<Brand>> getAllBrands();

    ServiceResult<Brand> updateBrand(int id, String name, String description);

    ServiceResult<Void> deleteBrand(int id);
}
