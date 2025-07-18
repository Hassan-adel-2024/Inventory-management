package com.inventoryapp.inventorymanagement.dao.impl;

import com.inventoryapp.inventorymanagement.dao.ProductDao;
import com.inventoryapp.inventorymanagement.db.DatabaseConfig;
import com.inventoryapp.inventorymanagement.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductDaoImpl implements ProductDao {
    @Override
    public void save(Product product) throws SQLException {
        String sql = "INSERT INTO Products (Name, CurrentStock," +
                " ReorderThreshold, UnitPrice, SupplierID) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, product.getName());
            stmt.setInt(2, product.getCurrentStock());
            stmt.setInt(3, product.getReorderThreshold());
            stmt.setDouble(4, product.getUnitPrice());
            stmt.setInt(5, product.getSupplierId());

            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setProductId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public Product findById(int id) throws SQLException {
        String sql = "SELECT * FROM Products WHERE ProductID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToProduct(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Product> findAll() throws SQLException {
        String sql = "SELECT * FROM Products ORDER BY Name";
        List<Product> products = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        return products;
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("productID"));
        product.setName(rs.getString("Name"));
        product.setCurrentStock(rs.getInt("currentStock"));
        product.setReorderThreshold(rs.getInt("reorderThreshold"));
        product.setUnitPrice(rs.getDouble("unitPrice"));
        product.setSupplierId(rs.getInt("supplierID"));
        return product;
    }


    @Override
    public void update(Product product) throws SQLException {
        String sql = "UPDATE Products SET Name = ?, CurrentStock = ?," +
                " ReorderThreshold = ?, UnitPrice = ?, SupplierID = ? WHERE ProductID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, product.getName());
            stmt.setInt(2, product.getCurrentStock());
            stmt.setInt(3, product.getReorderThreshold());
            stmt.setDouble(4, product.getUnitPrice());
            stmt.setInt(5, product.getSupplierId());
            stmt.setInt(6, product.getProductId());

            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Products WHERE ProductID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
    public void updateProductStock(int productId, int newStock) throws SQLException {
        String sql = "UPDATE Products SET CurrentStock = ? WHERE ProductID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, newStock);
            stmt.setInt(2, productId);
            stmt.executeUpdate();
        }
    }
    public List<Product> getProductsBelowReorderThreshold() throws SQLException {
        String sql = "SELECT * FROM Products WHERE CurrentStock < ReorderThreshold";
        List<Product> products = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                products.add(mapResultSetToProduct(rs));
            }
        }
        return products;
    }
    public List<Product> getProductsBySupplierId(int supplierId) throws SQLException {
        String sql = "SELECT * FROM Products WHERE SupplierID = ?";
        List<Product> products = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, supplierId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        }
        return products;
    }
    public int getProductStock(int productId) throws SQLException {
        String sql = "SELECT CurrentStock FROM Products WHERE ProductID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("CurrentStock");
                }
            }
        }
        return 0;
    }

    @Override
    public void updateMultipleProductStocks(Map<Integer, Integer> productStockUpdates) throws SQLException {
        String sql = "UPDATE Products SET CurrentStock = ? WHERE ProductID = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (Map.Entry<Integer, Integer> entry : productStockUpdates.entrySet()) {
                stmt.setInt(1, entry.getValue());
                stmt.setInt(2, entry.getKey());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    @Override
    public List<Product> getProductsByIds(List<Integer> productIds) throws SQLException {
        if (productIds.isEmpty()) {
            return new ArrayList<>();
        }

        StringBuilder sql = new StringBuilder("SELECT * FROM Products WHERE ProductID IN (");
        for (int i = 0; i < productIds.size(); i++) {
            if (i > 0) sql.append(",");
            sql.append("?");
        }
        sql.append(")");

        List<Product> products = new ArrayList<>();
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < productIds.size(); i++) {
                stmt.setInt(i + 1, productIds.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    products.add(mapResultSetToProduct(rs));
                }
            }
        }
        return products;
    }

    @Override
    public boolean validateStockAvailability(int productId, int quantity) throws SQLException {
        String sql = "SELECT CurrentStock FROM Products WHERE ProductID = ? AND CurrentStock >= ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, productId);
            stmt.setInt(2, quantity);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next(); // Returns true if product exists and has sufficient stock
            }
        }
    }
}
