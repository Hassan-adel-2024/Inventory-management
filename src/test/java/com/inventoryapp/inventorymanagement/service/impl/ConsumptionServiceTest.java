package com.inventoryapp.inventorymanagement.service.impl;

import com.inventoryapp.inventorymanagement.dto.ConsumptionResponseDto;
import com.inventoryapp.inventorymanagement.model.Product;
import com.inventoryapp.inventorymanagement.service.IProductService;
import javafx.util.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsumptionServiceTest {

    @Mock
    private IProductService productService;

    private ConsumptionService consumptionService;

    @BeforeEach
    void setUp() {
        consumptionService = new ConsumptionService(productService);
    }

    @Test
    void testConsumeProduct_ShouldConsumeProductsSuccessfully_WhenValidData() {
        // Arrange
        List<Pair<Integer, Integer>> productQuantities = Arrays.asList(
                new Pair<>(1, 5),
                new Pair<>(2, 3)
        );

        List<Product> products = Arrays.asList(
                createProduct(1, "Product 1", 10, 5, 25.0, 1),
                createProduct(2, "Product 2", 8, 3, 30.0, 2)
        );

        when(productService.getProductsByIds(Arrays.asList(1, 2))).thenReturn(products);
        when(productService.validateStockAvailability(1, 5)).thenReturn(true);
        when(productService.validateStockAvailability(2, 3)).thenReturn(true);

        // Act
        ConsumptionResponseDto result = consumptionService.consumeProduct(productQuantities);

        // Assert
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("✓ Products consumed successfully"));
        assertTrue(result.getMessage().contains("Product 1 (-5)"));
        assertTrue(result.getMessage().contains("Product 2 (-3)"));
        
        verify(productService).getProductsByIds(Arrays.asList(1, 2));
        verify(productService).validateStockAvailability(1, 5);
        verify(productService).validateStockAvailability(2, 3);
        verify(productService).updateMultipleProductStocks(any());
    }


    @Test
    void testConsumeProduct_ShouldFail_WhenInsufficientStock() {
        // Arrange
        List<Pair<Integer, Integer>> productQuantities = Arrays.asList(
                new Pair<>(1, 15)
        );

        List<Product> products = Arrays.asList(
                createProduct(1, "Product 1", 10, 5, 25.0, 1)
        );

        when(productService.getProductsByIds(Arrays.asList(1))).thenReturn(products);
        when(productService.validateStockAvailability(1, 15)).thenReturn(false);

        // Act
        ConsumptionResponseDto result = consumptionService.consumeProduct(productQuantities);

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Insufficient stock for product: Product 1"));
        
        verify(productService).getProductsByIds(Arrays.asList(1));
        verify(productService).validateStockAvailability(1, 15);
        verify(productService, never()).updateMultipleProductStocks(any());
    }

    @Test
    void testConsumeProduct_ShouldShowLowStockWarning_WhenStockBelowThreshold() {
        // Arrange
        List<Pair<Integer, Integer>> productQuantities = Arrays.asList(
                new Pair<>(1, 8)
        );

        List<Product> products = Arrays.asList(
                createProduct(1, "Product 1", 10, 5, 25.0, 1)
        );

        when(productService.getProductsByIds(Arrays.asList(1))).thenReturn(products);
        when(productService.validateStockAvailability(1, 8)).thenReturn(true);

        // Act
        ConsumptionResponseDto result = consumptionService.consumeProduct(productQuantities);

        // Assert
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("⚠ Low stock warnings"));
        assertTrue(result.getMessage().contains("Product 1 is now below reorder threshold (2 remaining)"));
        
        verify(productService).getProductsByIds(Arrays.asList(1));
        verify(productService).validateStockAvailability(1, 8);
        verify(productService).updateMultipleProductStocks(any());
    }

    @Test
    void testConsumeProduct_ShouldHandleEmptyList() {
        // Arrange
        List<Pair<Integer, Integer>> productQuantities = Arrays.asList();

        // Act
        ConsumptionResponseDto result = consumptionService.consumeProduct(productQuantities);

        // Assert
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("✓ Products consumed successfully"));
        // Accept that getProductsByIds and updateMultipleProductStocks are called with empty arguments
        verify(productService).getProductsByIds(any());
        verify(productService, never()).validateStockAvailability(anyInt(), anyInt());
        verify(productService).updateMultipleProductStocks(any());
    }

    @Test
    void testConsumeProduct_ShouldHandleMultipleProductsWithMixedScenarios() {
        // Arrange
        List<Pair<Integer, Integer>> productQuantities = Arrays.asList(
                new Pair<>(1, 3), // Normal consumption
                new Pair<>(2, 8), // Will trigger low stock warning
                new Pair<>(3, 2)  // Normal consumption
        );

        List<Product> products = Arrays.asList(
                createProduct(1, "Product 1", 10, 5, 25.0, 1),
                createProduct(2, "Product 2", 10, 5, 30.0, 2),
                createProduct(3, "Product 3", 15, 8, 35.0, 3)
        );

        when(productService.getProductsByIds(Arrays.asList(1, 2, 3))).thenReturn(products);
        when(productService.validateStockAvailability(1, 3)).thenReturn(true);
        when(productService.validateStockAvailability(2, 8)).thenReturn(true);
        when(productService.validateStockAvailability(3, 2)).thenReturn(true);

        // Act
        ConsumptionResponseDto result = consumptionService.consumeProduct(productQuantities);

        // Assert
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("✓ Products consumed successfully"));
        assertTrue(result.getMessage().contains("Product 1 (-3)"));
        assertTrue(result.getMessage().contains("Product 2 (-8)"));
        assertTrue(result.getMessage().contains("Product 3 (-2)"));
        assertTrue(result.getMessage().contains("⚠ Low stock warnings"));
        assertTrue(result.getMessage().contains("Product 2 is now below reorder threshold (2 remaining)"));
        
        verify(productService).getProductsByIds(Arrays.asList(1, 2, 3));
        verify(productService).validateStockAvailability(1, 3);
        verify(productService).validateStockAvailability(2, 8);
        verify(productService).validateStockAvailability(3, 2);
        verify(productService).updateMultipleProductStocks(any());
    }

    @Test
    void testConsumeProduct_ShouldHandleExceptionGracefully() {
        // Arrange
        List<Pair<Integer, Integer>> productQuantities = Arrays.asList(
                new Pair<>(1, 5)
        );

        when(productService.getProductsByIds(Arrays.asList(1))).thenThrow(new RuntimeException("Service error"));

        // Act
        ConsumptionResponseDto result = consumptionService.consumeProduct(productQuantities);

        // Assert
        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Consumption failed: Service error"));
        
        verify(productService).getProductsByIds(Arrays.asList(1));
        verify(productService, never()).validateStockAvailability(anyInt(), anyInt());
        verify(productService, never()).updateMultipleProductStocks(any());
    }

    @Test
    void testConsumeProduct_ShouldHandleZeroQuantity() {
        // Arrange
        List<Pair<Integer, Integer>> productQuantities = Arrays.asList(
                new Pair<>(1, 0),
                new Pair<>(2, 5)
        );

        List<Product> products = Arrays.asList(
                createProduct(1, "Product 1", 10, 5, 25.0, 1),
                createProduct(2, "Product 2", 8, 3, 30.0, 2)
        );

        when(productService.getProductsByIds(Arrays.asList(1, 2))).thenReturn(products);
        when(productService.validateStockAvailability(1, 0)).thenReturn(true);
        when(productService.validateStockAvailability(2, 5)).thenReturn(true);

        // Act
        ConsumptionResponseDto result = consumptionService.consumeProduct(productQuantities);

        // Assert
        assertTrue(result.isSuccess());
        assertTrue(result.getMessage().contains("Product 1 (-0)"));
        assertTrue(result.getMessage().contains("Product 2 (-5)"));
        
        verify(productService).getProductsByIds(Arrays.asList(1, 2));
        verify(productService).validateStockAvailability(1, 0);
        verify(productService).validateStockAvailability(2, 5);
        verify(productService).updateMultipleProductStocks(any());
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