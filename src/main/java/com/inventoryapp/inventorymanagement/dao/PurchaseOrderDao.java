package com.inventoryapp.inventorymanagement.dao;

import com.inventoryapp.inventorymanagement.model.PurchaseOrder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrderDao extends BaseDao<PurchaseOrder>{

    @Override
    public void save(PurchaseOrder purchaseOrder) throws SQLException {
        String sql = "INSERT INTO PurchaseOrder (SupplierID, CreatedAt) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, purchaseOrder.getSupplierID());
            stmt.setDate(2, new java.sql.Date(purchaseOrder.getCreatedAt().getTime()));
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
        String sql = "SELECT * FROM PurchaseOrder WHERE OrderID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new PurchaseOrder(
                            rs.getInt("OrderID"),
                            rs.getInt("SupplierID"),
                            rs.getDate("CreatedAt")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<PurchaseOrder> findAll() throws SQLException {
        String sql = "SELECT * FROM PurchaseOrder";
        List<PurchaseOrder> orders = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orders.add(new PurchaseOrder(
                        rs.getInt("OrderID"),
                        rs.getInt("SupplierID"),
                        rs.getDate("CreatedAt")
                ));
            }
        }
        return orders;
    }

    @Override
    public void update(PurchaseOrder purchaseOrder) throws SQLException {
        String sql = "UPDATE PurchaseOrder SET SupplierID = ?, CreatedAt = ? WHERE OrderID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, purchaseOrder.getSupplierID());
            stmt.setDate(2, new java.sql.Date(purchaseOrder.getCreatedAt().getTime()));
            stmt.setInt(3, purchaseOrder.getOrderID());

            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM PurchaseOrder WHERE OrderID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

}
