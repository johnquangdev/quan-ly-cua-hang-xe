/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.quanlycuahangxe;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;

/**
 *
 * @author gunnguyen
 */
public class FlywayMigrator {

    public static void runMigration() {
        try {
            String URL = com.quanlycuahangxe.config.LoadEnv.getDbUrl();
            String USERNAME = com.quanlycuahangxe.config.LoadEnv.getDbUser();
            String PASSWORD = com.quanlycuahangxe.config.LoadEnv.getDbPassword();
            Flyway flyway = Flyway.configure()
                    .dataSource(URL, USERNAME, PASSWORD)
                    .locations("classpath:migration") // đổi đúng path nếu bạn dùng thư mục khác
                    .load();

            flyway.migrate();

            System.out.println("Flyway migration completed successfully.");
        } catch (FlywayException e) {
            System.err.println("Flyway migration failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error during migration:");
            e.printStackTrace();
        }
    }
}
