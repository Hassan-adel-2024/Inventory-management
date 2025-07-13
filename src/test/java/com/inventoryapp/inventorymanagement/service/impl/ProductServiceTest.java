package com.inventoryapp.inventorymanagement.service.impl;

import com.inventoryapp.inventorymanagement.dao.ProductDao;
import com.inventoryapp.inventorymanagement.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductDao productDao;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productDao);
    }

    @Test
    void testGetAllProducts_ShouldReturnProducts_WhenDaoReturnsData() throws SQLException {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(
                createProduct(1, "Product 1", 10, 5, 25.0, 1),
                createProduct(2, "Product 2", 20, 10, 30.0, 2)
        );
        when(productDao.findAll()).thenReturn(expectedProducts);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertEquals(expectedProducts, result);
        verify(productDao).findAll();
    }


    @Test
    void testSaveProduct_ShouldCallDao_WhenValidProduct() throws SQLException {
        // Arrange
        Product product = createProduct(1, "Test Product", 10, 5, 25.0, 1);

        // Act
        productService.saveProduct(product);

        // Assert
        verify(productDao).save(product);
    }


    @Test
    void testGetProductById_ShouldReturnNull_WhenProductDoesNotExist() throws SQLException {
        // Arrange
        when(productDao.findById(999)).thenReturn(null);

        // Act
        Product result = productService.getProductById(999);

        // Assert
        assertNull(result);
        verify(productDao).findById(999);
    }

    @Test
    void testGetProductById_ShouldReturnNull_WhenDaoThrowsException() throws SQLException {
        // Arrange
        when(productDao.findById(1)).thenThrow(new SQLException("Database error"));

        // Act
        Product result = productService.getProductById(1);

        // Assert
        assertNull(result);
        verify(productDao).findById(1);
    }

    @Test
    void testUpdateProduct_ShouldCallDao_WhenValidProduct() throws SQLException {
        // Arrange
        Product product = createProduct(1, "Updated Product", 15, 8, 30.0, 2);

        // Act
        productService.updateProduct(product);

        // Assert
        verify(productDao).update(product);
    }

    @Test
    void testUpdateProduct_ShouldThrowRuntimeException_WhenDaoThrowsException() throws SQLException {
        // Arrange
        Product product = createProduct(1, "Updated Product", 15, 8, 30.0, 2);
        doThrow(new SQLException("Database error")).when(productDao).update(product);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            productService.updateProduct(product);
        });
        verify(productDao).update(product);
    }

    @Test
    void testDeleteProduct_ShouldCallDao_WhenValidId() throws SQLException {
        // Arrange
        int productId = 1;

        // Act
        productService.deleteProduct(productId);

        // Assert
        verify(productDao).delete(productId);
    }

    @Test
    void testUpdateProducts_ShouldCallDao_WhenValidData() throws SQLException {
        // Arrange
        int productId = 1;
        int newStock = 25;

        // Act
        productService.updateProducts(productId, newStock);

        // Assert
        verify(productDao).updateProductStock(productId, newStock);
    }

    @Test
    void testUpdateProducts_ShouldThrowRuntimeException_WhenDaoThrowsException() throws SQLException {
        // Arrange
        int productId = 1;
        int newStock = 25;
        doThrow(new SQLException("Database error")).when(productDao).updateProductStock(productId, newStock);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            productService.updateProducts(productId, newStock);
        });
        verify(productDao).updateProductStock(productId, newStock);
    }

    @Test
    void testGetProductsBelowThreshold_ShouldReturnLowStockProducts() throws SQLException {
        // Arrange
        List<Product> expectedProducts = Arrays.asList(
                createProduct(1, "Low Stock Product", 3, 5, 25.0, 1),
                createProduct(2, "Another Low Stock", 2, 10, 30.0, 2)
        );
        when(productDao.getProductsBelowReorderThreshold()).thenReturn(expectedProducts);

        // Act
        List<Product> result = productService.getProductsBelowThreshold();

        // Assert
        assertEquals(expectedProducts, result);
        verify(productDao).getProductsBelowReorderThreshold();
    }


    @Test
    void testGetProductsByIds_ShouldReturnProducts_WhenValidIds() throws SQLException {
        // Arrange
        List<Integer> productIds = Arrays.asList(1, 2, 3);
        List<Product> expectedProducts = Arrays.asList(
                createProduct(1, "Product 1", 10, 5, 25.0, 1),
                createProduct(2, "Product 2", 20, 10, 30.0, 2)
        );
        when(productDao.getProductsByIds(productIds)).thenReturn(expectedProducts);

        // Act
        List<Product> result = productService.getProductsByIds(productIds);

        // Assert
        assertEquals(expectedProducts, result);
        verify(productDao).getProductsByIds(productIds);
    }



    @Test
    void testValidateStockAvailability_ShouldReturnTrue_WhenSufficientStock() throws SQLException {
        // Arrange
        when(productDao.validateStockAvailability(1, 5)).thenReturn(true);

        // Act
        boolean result = productService.validateStockAvailability(1, 5);

        // Assert
        assertTrue(result);
        verify(productDao).validateStockAvailability(1, 5);
    }

    @Test
    void testValidateStockAvailability_ShouldReturnFalse_WhenInsufficientStock() throws SQLException {
        // Arrange
        when(productDao.validateStockAvailability(1, 100)).thenReturn(false);

        // Act
        boolean result = productService.validateStockAvailability(1, 100);

        // Assert
        assertFalse(result);
        verify(productDao).validateStockAvailability(1, 100);
    }

    // Helper method to create test products
    private Product createProduct(int id, String name, int stock, int threshold, double price, int supplierId) {
        Product product = new Product();
        product.setProductId(id);
        product.setName(name);
        product.setCurrentStock(stock);
        product.setReorderThreshold(threshold);
        product.setUnitPrice(price);
        product.setSupplierId(supplierId);
        return product;
    }
} 