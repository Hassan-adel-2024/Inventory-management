package com.inventoryapp.inventorymanagement.dao;

import com.inventoryapp.inventorymanagement.model.Product;

import java.sql.SQLException;
import java.util.List;

public interface ProductDao extends BaseDao<Product>{
    void updateProductStock(int productId, int newStock) throws SQLException;
    List<Product> getProductsBelowReorderThreshold() throws SQLException;
    List<Product> getProductsBySupplierId(int supplierId) throws SQLException;
    int getProductStock(int productId) throws SQLException;
}
