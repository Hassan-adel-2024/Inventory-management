package com.inventoryapp.inventorymanagement.service.impl;

import com.inventoryapp.inventorymanagement.beanfactory.DaoBeanFactory;
import com.inventoryapp.inventorymanagement.dao.ProductDao;
import com.inventoryapp.inventorymanagement.model.Product;
import com.inventoryapp.inventorymanagement.service.IProductService;

import java.util.List;

public class ProductService implements IProductService {
    //    private final ProductDaoImpl productDaoImpl = new ProductDaoImpl();
    // I used ProductDao interface instead of implementation class for proper abstraction and decoupling
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

    }

    public void updateProducts(int productId, int newStock) {
        try {
            Product product = getProductById(productId);
            if (product != null) {
                product.setCurrentStock(newStock);
                updateProduct(product);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Product> getProductsBelowThreshold() {
        try {
            return productDao.findAll().stream()
                    .filter(p -> p.getCurrentStock() < p.getReorderThreshold())
                    .toList();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }
}
