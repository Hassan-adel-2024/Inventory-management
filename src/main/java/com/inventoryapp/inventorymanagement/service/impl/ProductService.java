package com.inventoryapp.inventorymanagement.service.impl;

import com.inventoryapp.inventorymanagement.beanfactory.DaoBeanFactory;
import com.inventoryapp.inventorymanagement.dao.ProductDao;
import com.inventoryapp.inventorymanagement.model.Product;
import com.inventoryapp.inventorymanagement.service.IProductService;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductService implements IProductService {
    private static final Logger logger = Logger.getLogger(ProductService.class.getName());
    private final ProductDao productDao;

    public ProductService() {
        this.productDao = DaoBeanFactory.getInstance().getDao(ProductDao.class);
    }

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public List<Product> getAllProducts() {
        try {
            return productDao.findAll();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to retrieve all products", e);
            return List.of();
        }
    }

    @Override
    public void saveProduct(Product product) {
        try {
            productDao.save(product);
            logger.info("Product saved successfully: " + product.getName());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to save product: " + product.getName(), e);
            throw new RuntimeException("Failed to save product", e);
        }
    }

    @Override
    public Product getProductById(int id) {
        try {
            return productDao.findById(id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to retrieve product by ID: " + id, e);
            return null;
        }
    }

    @Override
    public void updateProduct(Product product) {
        try {
            productDao.update(product);
            logger.info("Product updated successfully: " + product.getName());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to update product: " + product.getName(), e);
            throw new RuntimeException("Failed to update product", e);
        }
    }

    @Override
    public void deleteProduct(int id) {
        try {
            productDao.delete(id);
            logger.info("Product deleted successfully: ID " + id);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to delete product: ID " + id, e);
            throw new RuntimeException("Failed to delete product", e);
        }
    }

    public void updateProducts(int productId, int newStock) {
        try {
            productDao.updateProductStock(productId, newStock);
            logger.info("Product stock updated: ID " + productId + ", New stock: " + newStock);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to update product stock: ID " + productId, e);
            throw new RuntimeException("Failed to update product stock", e);
        }
    }
    @Override
    public List<Product> getProductsBelowThreshold() {
        try {
            // Now using SQL WHERE clause instead of Java stream filtering
            return productDao.getProductsBelowReorderThreshold();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to retrieve products below threshold", e);
            return List.of();
        }
    }

    @Override
    public void updateMultipleProductStocks(Map<Integer, Integer> productStockUpdates) {
        try {
            productDao.updateMultipleProductStocks(productStockUpdates);
            logger.info("Stock update completed for " + productStockUpdates.size() + " products");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to update multiple product stocks", e);
            throw new RuntimeException("Failed to update multiple product stocks", e);
        }
    }

    @Override
    public List<Product> getProductsByIds(List<Integer> productIds) {
        try {
            return productDao.getProductsByIds(productIds);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to retrieve products by IDs", e);
            return List.of();
        }
    }

    @Override
    public boolean validateStockAvailability(int productId, int quantity) {
        try {
            return productDao.validateStockAvailability(productId, quantity);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to validate stock availability for product ID: " + productId, e);
            return false;
        }
    }
}
