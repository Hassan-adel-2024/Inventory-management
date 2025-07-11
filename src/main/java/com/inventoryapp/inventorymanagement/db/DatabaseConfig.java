package com.inventoryapp.inventorymanagement.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=RestaurantInventorySystem;encrypt=false";

    private static final String USER = "hassan";         // Replace with your SQL Server username
    private static final String PASSWORD = "admin"; // Replace with your password

    private static Connection connection;

    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connected to the database successfully.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to the database:");
            e.printStackTrace();
        }
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("🔌 Connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
