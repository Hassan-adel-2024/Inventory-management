package com.inventoryapp.inventorymanagement.dao.impl;

import com.inventoryapp.inventorymanagement.dao.PurchaseOrderDao;
import com.inventoryapp.inventorymanagement.model.PurchaseOrder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.inventoryapp.inventorymanagement.db.DatabaseConfig.getConnection;

public class PurchaseOrderDaoImpl implements PurchaseOrderDao {

    @Override
    public void save(PurchaseOrder purchaseOrder) throws SQLException {
        String sql = "INSERT INTO PurchaseOrders (SupplierID, CreatedAt, IsDelivered, IsDeleted) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, purchaseOrder.getSupplierID());
            stmt.setDate(2, new java.sql.Date(purchaseOrder.getCreatedAt().getTime()));
            stmt.setBoolean(3, purchaseOrder.isDelivered());
            stmt.setBoolean(4, purchaseOrder.isDeleted());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    purchaseOrder.setOrderID(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public PurchaseOrder findById(int id) throws SQLException {
        String sql = "SELECT * FROM PurchaseOrders WHERE OrderID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new PurchaseOrder(
                            rs.getInt("OrderID"),
                            rs.getInt("SupplierID"),
                            rs.getDate("CreatedAt"),
                            rs.getBoolean("IsDelivered"),
                            rs.getBoolean("IsDeleted")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<PurchaseOrder> findAll() throws SQLException {
        String sql = "SELECT * FROM PurchaseOrders";
        List<PurchaseOrder> orders = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orders.add(new PurchaseOrder(
                        rs.getInt("OrderID"),
                        rs.getInt("SupplierID"),
                        rs.getDate("CreatedAt"),
                        rs.getBoolean("IsDelivered"),
                        rs.getBoolean("IsDeleted")
                ));
            }
        }
        return orders;
    }

    @Override
    public void update(PurchaseOrder purchaseOrder) throws SQLException {
        String sql = "UPDATE PurchaseOrders SET SupplierID = ?, CreatedAt = ?, IsDelivered = ?, IsDeleted = ? WHERE OrderID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, purchaseOrder.getSupplierID());
            stmt.setDate(2, new java.sql.Date(purchaseOrder.getCreatedAt().getTime()));
            stmt.setBoolean(3, purchaseOrder.isDelivered());
            stmt.setBoolean(4, purchaseOrder.isDeleted());
            stmt.setInt(5, purchaseOrder.getOrderID());

            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM PurchaseOrders WHERE OrderID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

}
