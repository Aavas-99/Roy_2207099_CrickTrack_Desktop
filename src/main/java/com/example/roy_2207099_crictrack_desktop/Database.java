package com.example.roy_2207099_crictrack_desktop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private static final String DB_URL = "jdbc:sqlite:cricktrack.db";

    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
            return conn;
        } catch (SQLException e) {
            System.out.println("Database Connection Failed!");
            e.printStackTrace();
            return null;
        }
    }
    public static void init() {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Database connected successfully.");
                try (var stmt = conn.createStatement()) {
                    String sql = "CREATE TABLE IF NOT EXISTS users (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "username TEXT NOT NULL UNIQUE," +
                            "password TEXT NOT NULL" +
                            ");";
                    stmt.execute(sql);
                    System.out.println("Users table ensured.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Failed to initialize database.");
            e.printStackTrace();
        }
    }
}
