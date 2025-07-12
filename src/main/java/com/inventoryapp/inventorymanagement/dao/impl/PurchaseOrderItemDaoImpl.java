package com.inventoryapp.inventorymanagement.dao.impl;

import com.inventoryapp.inventorymanagement.dao.PurchaseOrderItemDao;
import com.inventoryapp.inventorymanagement.model.PurchaseOrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.inventoryapp.inventorymanagement.db.DatabaseConfig.getConnection;

public class PurchaseOrderItemDaoImpl implements PurchaseOrderItemDao {
    @Override
    public void save(PurchaseOrderItem item) throws SQLException {
        String sql = "INSERT INTO OrderItems (OrderID, ProductID, UnitPrice, Quantity) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {



            stmt.setInt(1, item.getOrderID());
            stmt.setInt(2, item.getProductID());
            stmt.setDouble(3, item.getUnitPrice());
            stmt.setInt(4, item.getQuantity());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setOrderItemID(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public PurchaseOrderItem findById(int id) throws SQLException {
        String sql = "SELECT * FROM OrderItems WHERE OrderItemID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new PurchaseOrderItem(
                            rs.getInt("OrderItemID"),
                            rs.getInt("OrderID"),
                            rs.getInt("ProductID"),
                            rs.getDouble("UnitPrice"),
                            rs.getInt("Quantity")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<PurchaseOrderItem> findAll() throws SQLException {
        String sql = "SELECT * FROM OrderItems";
        List<PurchaseOrderItem> items = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                items.add(new PurchaseOrderItem(
                        rs.getInt("OrderItemID"),
                        rs.getInt("OrderID"),
                        rs.getInt("ProductID"),
                        rs.getDouble("UnitPrice"),
                        rs.getInt("Quantity")
                ));
            }
        }

        return items;
    }

    @Override
    public void update(PurchaseOrderItem item) throws SQLException {
        String sql = "UPDATE OrderItems SET OrderID = ?, ProductID = ?, UnitPrice = ?, Quantity = ? WHERE OrderItemID = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, item.getOrderID());
            stmt.setInt(2, item.getProductID());
            stmt.setDouble(3, item.getUnitPrice());
            stmt.setInt(4, item.getQuantity());
            stmt.setInt(5, item.getOrderItemID());

            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {

        String sql = "DELETE FROM OrderItems WHERE OrderItemID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
