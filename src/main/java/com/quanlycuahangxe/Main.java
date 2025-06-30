/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe;

import com.quanlycuahangxe.db.NewConnectPostgres;
import java.sql.Connection;

/**
 *
 * @author gunnguyen
 */
public class Main {

    public static void main(String[] args) {
        //connect to db
        Connection conn = NewConnectPostgres.getConnection();
        if (conn != null) {
            System.out.println("Connected to PostgreSQL!");
        } else {
            System.out.println("Connection failed!");
            return;
        }

        // run Flyway Migration
        FlywayMigrator.runMigration();
    }
}
