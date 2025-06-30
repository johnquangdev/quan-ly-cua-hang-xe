/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe.dao;

import com.quanlycuahangxe.db.NewConnectPostgres;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author gunnguyen
 */
class DbHelper {

    public static PreparedStatement prepare(String sql) throws SQLException {
        Connection conn = NewConnectPostgres.getConnection();
        return conn.prepareStatement(sql);
    }
}
