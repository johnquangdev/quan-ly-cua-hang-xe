/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.quanlycuahangxe.service.interfaces;

import com.quanlycuahangxe.model.SystemManagement;
import com.quanlycuahangxe.exception.SystemManagementFoundException;
import com.quanlycuahangxe.exception.DuplicateSystemManagementException;

import java.util.List;

/**
 *
 * @author Minh
 */
public interface SystemManagementService {
    SystemManagement createSystem(SystemManagement system) throws DuplicateSystemManagementException;
    SystemManagement getSystemById(Long id) throws SystemManagementFoundException;
    List<SystemManagement> getAllSystems();
    SystemManagement updateSystem(Long id, SystemManagement system) throws SystemManagementFoundException;
    void deleteSystem(Long id) throws SystemManagementFoundException;
    boolean existsByName(String systemName);
}