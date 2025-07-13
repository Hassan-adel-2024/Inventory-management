package com.inventoryapp.inventorymanagement.dao;

import com.inventoryapp.inventorymanagement.model.Product;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface ProductDao extends BaseDao<Product>{
    void updateProductStock(int productId, int newStock) throws SQLException;
    List<Product> getProductsBelowReorderThreshold() throws SQLException;
    List<Product> getProductsBySupplierId(int supplierId) throws SQLException;
    int getProductStock(int productId) throws SQLException;
    void updateMultipleProductStocks(Map<Integer, Integer> productStockUpdates) throws SQLException;
    List<Product> getProductsByIds(List<Integer> productIds) throws SQLException;
    boolean validateStockAvailability(int productId, int quantity) throws SQLException;
}
