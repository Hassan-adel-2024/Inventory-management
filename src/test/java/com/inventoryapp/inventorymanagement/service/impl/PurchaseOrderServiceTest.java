package com.inventoryapp.inventorymanagement.service.impl;

import com.inventoryapp.inventorymanagement.dao.PurchaseOrderDao;
import com.inventoryapp.inventorymanagement.dao.PurchaseOrderItemDao;
import com.inventoryapp.inventorymanagement.model.Product;
import com.inventoryapp.inventorymanagement.model.PurchaseOrder;
import com.inventoryapp.inventorymanagement.model.PurchaseOrderItem;
import com.inventoryapp.inventorymanagement.service.IProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseOrderServiceTest {

    @Mock
    private PurchaseOrderDao purchaseOrderDao;

    @Mock
    private PurchaseOrderItemDao purchaseOrderItemDao;

    @Mock
    private IProductService productService;

    private PurchaseOrderService purchaseOrderService;

    @BeforeEach
    void setUp() {
        purchaseOrderService = new PurchaseOrderService(purchaseOrderDao, purchaseOrderItemDao, productService);
    }

    @Test
    void testCreatePurchaseOrdersForLowStockProducts_ShouldCreateNewOrder_WhenNoUndeliveredItems() throws SQLException {
        // Arrange
        List<Product> lowStockProducts = Arrays.asList(
                createProduct(1, "Low Stock Product", 3, 5, 25.0, 1)
        );

        when(productService.getProductsBelowThreshold()).thenReturn(lowStockProducts);
        when(purchaseOrderItemDao.findUndeliveredItemsByProductId(1)).thenReturn(Arrays.asList());
        doAnswer(invocation -> {
            PurchaseOrder order = invocation.getArgument(0);
            order.setOrderID(100);
            return null;
        }).when(purchaseOrderDao).save(any(PurchaseOrder.class));

        // Act
        List<String> result = purchaseOrderService.createPurchaseOrdersForLowStockProducts();

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).contains("Start order for Low Stock Product"));
        assertTrue(result.get(0).contains("Qty: 7")); // threshold * 2 - currentStock = 5 * 2 - 3 = 7
        
        verify(purchaseOrderDao).save(any(PurchaseOrder.class));
        verify(purchaseOrderItemDao).save(any(PurchaseOrderItem.class));
    }

    @Test
    void testCreatePurchaseOrdersForLowStockProducts_ShouldModifyExistingOrder_WhenUndeliveredItemsExist() throws SQLException {
        // Arrange
        List<Product> lowStockProducts = Arrays.asList(
                createProduct(1, "Low Stock Product", 3, 5, 25.0, 1)
        );

        PurchaseOrderItem existingItem = createOrderItem(1, 100, 1, 25.0, 5);
        List<PurchaseOrderItem> undeliveredItems = Arrays.asList(existingItem);

        when(productService.getProductsBelowThreshold()).thenReturn(lowStockProducts);
        when(purchaseOrderItemDao.findUndeliveredItemsByProductId(1)).thenReturn(undeliveredItems);

        // Act
        List<String> result = purchaseOrderService.createPurchaseOrdersForLowStockProducts();

        // Assert
        assertEquals(1, result.size());
        assertTrue(result.get(0).contains("Follow-up modification for Low Stock Product"));
        assertTrue(result.get(0).contains("Old Qty: 5"));
        assertTrue(result.get(0).contains("New Qty: 7"));
        
        verify(purchaseOrderItemDao).updateQuantity(1, 7);
    }


    @Test
    void testMarkOrderAsDelivered_ShouldReturnTrue_WhenOrderExistsAndNotDelivered() throws SQLException {
        // Arrange
        PurchaseOrder order = createOrder(1, 1, new Date(), false, false);
        List<PurchaseOrderItem> items = Arrays.asList(
                createOrderItem(1, 1, 1, 25.0, 5)
        );
        Product product = createProduct(1, "Test Product", 10, 5, 25.0, 1);

        when(purchaseOrderDao.findById(1)).thenReturn(order);
        when(purchaseOrderItemDao.findByOrderId(1)).thenReturn(items);
        when(productService.getProductById(1)).thenReturn(product);

        // Act
        boolean result = purchaseOrderService.markOrderAsDelivered(1);

        // Assert
        assertTrue(result);
        verify(purchaseOrderDao).markAsDelivered(1);
        verify(productService).updateProduct(any(Product.class));
    }

    @Test
    void testMarkOrderAsDelivered_ShouldReturnFalse_WhenOrderDoesNotExist() throws SQLException {
        // Arrange
        when(purchaseOrderDao.findById(999)).thenReturn(null);

        // Act
        boolean result = purchaseOrderService.markOrderAsDelivered(999);

        // Assert
        assertFalse(result);
        verify(purchaseOrderDao, never()).markAsDelivered(anyInt());
        verify(productService, never()).updateProduct(any(Product.class));
    }


    @Test
    void testMarkOrderAsDelivered_ShouldUpdateProductStock_WhenValidOrder() throws SQLException {
        // Arrange
        PurchaseOrder order = createOrder(1, 1, new Date(), false, false);
        List<PurchaseOrderItem> items = Arrays.asList(
                createOrderItem(1, 1, 1, 25.0, 5)
        );
        Product product = createProduct(1, "Test Product", 10, 5, 25.0, 1);

        when(purchaseOrderDao.findById(1)).thenReturn(order);
        when(purchaseOrderItemDao.findByOrderId(1)).thenReturn(items);
        when(productService.getProductById(1)).thenReturn(product);

        // Act
        purchaseOrderService.markOrderAsDelivered(1);

        // Assert
        verify(productService).updateProduct(argThat(p -> p.getCurrentStock() == 15)); // 10 + 5
    }


    @Test
    void testMarkOrderAsDeleted_ShouldReturnTrue_WhenOrderExistsAndNotDeleted() throws SQLException {
        // Arrange
        PurchaseOrder order = createOrder(1, 1, new Date(), false, false);
        when(purchaseOrderDao.findById(1)).thenReturn(order);

        // Act
        boolean result = purchaseOrderService.markOrderAsDeleted(1);

        // Assert
        assertTrue(result);
        verify(purchaseOrderDao).markAsDeleted(1);
    }

    @Test
    void testMarkOrderAsDeleted_ShouldReturnFalse_WhenOrderDoesNotExist() throws SQLException {
        // Arrange
        when(purchaseOrderDao.findById(999)).thenReturn(null);

        // Act
        boolean result = purchaseOrderService.markOrderAsDeleted(999);

        // Assert
        assertFalse(result);
        verify(purchaseOrderDao, never()).markAsDeleted(anyInt());
    }

    @Test
    void testMarkOrderAsDeleted_ShouldReturnFalse_WhenOrderAlreadyDeleted() throws SQLException {
        // Arrange
        PurchaseOrder order = createOrder(1, 1, new Date(), false, true);
        when(purchaseOrderDao.findById(1)).thenReturn(order);

        // Act
        boolean result = purchaseOrderService.markOrderAsDeleted(1);

        // Assert
        assertFalse(result);
        verify(purchaseOrderDao, never()).markAsDeleted(anyInt());
    }

    @Test
    void testMarkOrderAsDeleted_ShouldReturnFalse_WhenOrderIsDelivered() throws SQLException {
        // Arrange
        PurchaseOrder order = createOrder(1, 1, new Date(), true, false);
        when(purchaseOrderDao.findById(1)).thenReturn(order);

        // Act
        boolean result = purchaseOrderService.markOrderAsDeleted(1);

        // Assert
        assertFalse(result);
        verify(purchaseOrderDao, never()).markAsDeleted(anyInt());
    }

    @Test
    void testCreatePurchaseOrdersForLowStockProducts_ShouldHandleSQLException() throws SQLException {
        // Arrange
        List<Product> lowStockProducts = Arrays.asList(
                createProduct(1, "Low Stock Product", 3, 5, 25.0, 1)
        );

        when(productService.getProductsBelowThreshold()).thenReturn(lowStockProducts);
        when(purchaseOrderItemDao.findUndeliveredItemsByProductId(1)).thenThrow(new SQLException("Database error"));

        // Act & Assert
        assertThrows(SQLException.class, () -> purchaseOrderService.createPurchaseOrdersForLowStockProducts());
    }



    @Test
    void testMarkOrderAsDeleted_ShouldHandleSQLException() throws SQLException {
        // Arrange
        when(purchaseOrderDao.findById(1)).thenThrow(new SQLException("Database error"));

        // Act & Assert
        assertThrows(SQLException.class, () -> purchaseOrderService.markOrderAsDeleted(1));
    }

    // Helper methods to create test objects
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

    private PurchaseOrder createOrder(int orderId, int supplierId, Date createdAt, boolean delivered, boolean deleted) {
        PurchaseOrder order = new PurchaseOrder();
        order.setOrderID(orderId);
        order.setSupplierID(supplierId);
        order.setCreatedAt(createdAt);
        order.setDelivered(delivered);
        order.setDeleted(deleted);
        return order;
    }

    private PurchaseOrderItem createOrderItem(int itemId, int orderId, int productId, double unitPrice, int quantity) {
        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setOrderItemID(itemId);
        item.setOrderID(orderId);
        item.setProductID(productId);
        item.setUnitPrice(unitPrice);
        item.setQuantity(quantity);
        return item;
    }
} 