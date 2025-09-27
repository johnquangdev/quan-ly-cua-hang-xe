/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.quanlycuahangxe.service.interfaces;

/**
 *
 * @author gunnguyen
 */
import com.quanlycuahangxe.model.Staff;
import com.quanlycuahangxe.utils.ServiceResult;
import java.util.List;

public interface StaffService {

    ServiceResult<Staff> createStaff(int userId, String position, String phone);

    ServiceResult<Staff> getStaffById(int id);

    ServiceResult<List<Staff>> getAllStaffs();

    ServiceResult<Staff> updateStaff(int id, String position, String phone);

    ServiceResult<Void> deleteStaff(int id);
}
