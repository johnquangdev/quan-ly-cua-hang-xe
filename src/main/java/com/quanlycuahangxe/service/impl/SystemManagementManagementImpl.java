/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.service.impl;

import com.quanlycuahangxe.service.interfaces.SystemManagementService;
import com.quanlycuahangxe.dao.SystemManagementDao;
import com.quanlycuahangxe.model.SystemManagement;
import com.quanlycuahangxe.exception.SystemManagementFoundException;
import com.quanlycuahangxe.exception.DuplicateSystemManagementException;

import java.util.List;

/**
 *
 * @author Minh
 */
public class SystemManagementManagementImpl implements SystemManagementService {
    private final SystemManagementDao systemDao;

    public SystemManagementManagementImpl() {
        this.systemDao = new SystemManagementDao();
    }

    @Override
    public SystemManagement createSystem(SystemManagement system) throws DuplicateSystemManagementException {
        try {
            return systemDao.createSystem(system);
        } catch (DuplicateSystemManagementException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi tạo system management record: " + e.getMessage(), e);
        }
    }

    @Override
    public SystemManagement getSystemById(Long id) throws SystemManagementFoundException {
        try {
            return systemDao.getSystemById(id);
        } catch (SystemManagementFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy system management record: " + e.getMessage(), e);
        }
    }

    @Override
    public List<SystemManagement> getAllSystems() {
        try {
            return systemDao.getAllSystems();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi lấy danh sách system management: " + e.getMessage(), e);
        }
    }

    @Override
    public SystemManagement updateSystem(Long id, SystemManagement system) throws SystemManagementFoundException {
        try {
            return systemDao.updateSystem(id, system);
        } catch (SystemManagementFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi cập nhật system management record: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteSystem(Long id) throws SystemManagementFoundException {
        try {
            systemDao.deleteSystem(id);
        } catch (SystemManagementFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi xóa system management record: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsByName(String systemName) {
        try {
            return systemDao.existsByName(systemName);
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi kiểm tra tồn tại system management theo tên: " + e.getMessage(), e);
        }
    }
}