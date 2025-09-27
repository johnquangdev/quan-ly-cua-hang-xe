package com.quanlycuahangxe.config;

import io.github.cdimascio.dotenv.Dotenv;

public class LoadEnv {

    public static Dotenv dotenv = Dotenv.load();

    public static String getDbUrl() {
        return dotenv.get("DB_URL");
    }

    public static String getDbUser() {
        return dotenv.get("DB_USER");
    }

    public static String getDbPassword() {
        return dotenv.get("DB_PASSWORD");
    }

}
