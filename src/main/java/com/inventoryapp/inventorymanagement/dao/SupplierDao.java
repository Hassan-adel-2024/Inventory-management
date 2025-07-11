package com.inventoryapp.inventorymanagement.dao;

import com.inventoryapp.inventorymanagement.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupplierDao extends BaseDao<Supplier>{

    @Override
    public void save(Supplier supplier) throws SQLException {
        String sql = "INSERT INTO Suppliers (Name, Email, Phone, DeliveryTimeDays) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {


            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getEmail());
            stmt.setString(3, supplier.getPhone());
            stmt.setInt(4, supplier.getDeliveryTime());

            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    supplier.setSupplierId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public Supplier findById(int id) throws SQLException {
        String sql = "SELECT * FROM Suppliers WHERE SupplierID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Supplier(
                            rs.getInt("SupplierID"),
                            rs.getString("Name"),
                            rs.getString("Email"),
                            rs.getString("Phone"),
                            rs.getInt("DeliveryTimeDays")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<Supplier> findAll() throws SQLException {
        String sql = "SELECT * FROM Suppliers";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            List<Supplier> suppliers = new ArrayList<>();
            while (rs.next()) {
                suppliers.add(new Supplier(
                        rs.getInt("SupplierID"),
                        rs.getString("Name"),
                        rs.getString("Email"),
                        rs.getString("Phone"),
                        rs.getInt("DeliveryTimeDays")
                ));
            }
            return suppliers;
    }
    }

    @Override
    public void update(Supplier supplier) throws SQLException {
        String sql = "UPDATE Suppliers SET Name = ?, Email = ?, Phone = ?, DeliveryTimeDays = ? WHERE SupplierID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, supplier.getName());
            stmt.setString(2, supplier.getEmail());
            stmt.setString(3, supplier.getPhone());
            stmt.setInt(4, supplier.getDeliveryTime());
            stmt.setInt(5, supplier.getSupplierId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM Suppliers WHERE SupplierID = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
