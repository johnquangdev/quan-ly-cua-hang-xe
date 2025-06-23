/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.cuahangxe.quanlycuahangxe.gui;

import com.cuahangxe.quanlycuahangxe.dao.ImplPostgres.IAuth;

/**
 *
 * @author gunnguyen
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        IAuth auth = new IAuth();
        auth.testConnection();
    }

}
