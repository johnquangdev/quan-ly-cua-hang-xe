/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cuahangxe.quanlycuahangxe.dao.ImplPostgres;

import com.cuahangxe.quanlycuahangxe.db.NewConnecPostgres;
import java.sql.Connection;

/**
 *
 * @author gunnguyen
 */
public class IAuth {

    Connection conn = NewConnecPostgres.getConnection();

    public void testConnection() {
        if (conn != null) {
            System.out.println("Connected to PostgreSQL!");
        } else {
            System.out.println("Connection failed!");
        }
    }
}
