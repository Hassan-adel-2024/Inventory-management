package com.inventoryapp.inventorymanagement.service.impl;

import com.inventoryapp.inventorymanagement.dao.ProductDao;
import com.inventoryapp.inventorymanagement.exception.InvalidStockAmountException;
import com.inventoryapp.inventorymanagement.model.Product;
import com.inventoryapp.inventorymanagement.service.IProductService;

import java.util.List;

public class ProductService implements IProductService {
    private final ProductDao productDao = new ProductDao();
    @Override
    public List<Product> getAllProducts() {
        try {
            return productDao.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public void saveProduct(Product product) {
        try {
            productDao.save(product);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Product getProductById(int id) {
        try {
            return productDao.findById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void updateProduct(Product product) {
        try {
            productDao.update(product);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteProduct(int id) {
        try {
            productDao.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public List<Product> getProductsBelowThreshold() {
        try {
            return productDao.getProductsBelowReorderThreshold();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public void updateProductStock(int productId, int amount) {

        if(amount < 0) {
            throw new InvalidStockAmountException("Stock amount cannot be negative");
        }


        try {
            productDao.updateProductStock(productId, amount);
        } catch (Exception e) {
            System.err.println("Failed to update stock for product ID: " + productId);
            e.printStackTrace();
        }
    }


    private boolean isSufficientStock(int stockQuantity, int consumedQuantity) {
       return stockQuantity >= consumedQuantity;
    }

}
