package com.inventoryapp.inventorymanagement.service;

import com.inventoryapp.inventorymanagement.model.Product;

import java.util.List;
import java.util.Map;

public interface IProductService {
    List<Product> getAllProducts();
    void saveProduct(Product product);
    Product getProductById(int id);
    void updateProduct(Product product);
    void deleteProduct(int id);
    List<Product> getProductsBelowThreshold();
    void updateMultipleProductStocks(Map<Integer, Integer> productStockUpdates);
    List<Product> getProductsByIds(List<Integer> productIds);
    boolean validateStockAvailability(int productId, int quantity);
}
