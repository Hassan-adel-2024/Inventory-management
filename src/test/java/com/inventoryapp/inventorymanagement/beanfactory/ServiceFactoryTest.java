package com.inventoryapp.inventorymanagement.beanfactory;

import com.inventoryapp.inventorymanagement.service.IConsumptionService;
import com.inventoryapp.inventorymanagement.service.IProductService;
import com.inventoryapp.inventorymanagement.service.IPurchaseOrderService;
import com.inventoryapp.inventorymanagement.service.ISupplierService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ServiceFactoryTest {

    @Test
    void testGetInstance_ShouldReturnSameInstance() {
        // Act
        ServiceFactory instance1 = ServiceFactory.getInstance();
        ServiceFactory instance2 = ServiceFactory.getInstance();

        // Assert
        assertNotNull(instance1);
        assertNotNull(instance2);
        assertSame(instance1, instance2);
    }

    @Test
    void testGetService_ShouldReturnProductService() {
        // Arrange
        ServiceFactory factory = ServiceFactory.getInstance();

        // Act
        IProductService productService = factory.getService(IProductService.class);

        // Assert
        assertNotNull(productService);
        assertTrue(productService instanceof com.inventoryapp.inventorymanagement.service.impl.ProductService);
    }

    @Test
    void testGetService_ShouldReturnPurchaseOrderService() {
        // Arrange
        ServiceFactory factory = ServiceFactory.getInstance();

        // Act
        IPurchaseOrderService purchaseOrderService = factory.getService(IPurchaseOrderService.class);

        // Assert
        assertNotNull(purchaseOrderService);
        assertTrue(purchaseOrderService instanceof com.inventoryapp.inventorymanagement.service.impl.PurchaseOrderService);
    }

    @Test
    void testGetService_ShouldReturnSupplierService() {
        // Arrange
        ServiceFactory factory = ServiceFactory.getInstance();

        // Act
        ISupplierService supplierService = factory.getService(ISupplierService.class);

        // Assert
        assertNotNull(supplierService);
        assertTrue(supplierService instanceof com.inventoryapp.inventorymanagement.service.impl.SupplierService);
    }

    @Test
    void testGetService_ShouldReturnConsumptionService() {
        // Arrange
        ServiceFactory factory = ServiceFactory.getInstance();

        // Act
        IConsumptionService consumptionService = factory.getService(IConsumptionService.class);

        // Assert
        assertNotNull(consumptionService);
        assertTrue(consumptionService instanceof com.inventoryapp.inventorymanagement.service.impl.ConsumptionService);
    }

    @Test
    void testGetService_ShouldThrowException_WhenServiceNotRegistered() {
        // Arrange
        ServiceFactory factory = ServiceFactory.getInstance();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> factory.getService(String.class));
    }

    @Test
    void testRegisterService_ShouldAllowCustomServiceRegistration() {
        // Arrange
        ServiceFactory factory = ServiceFactory.getInstance();
        IProductService mockService = new com.inventoryapp.inventorymanagement.service.impl.ProductService();

        // Act
        factory.registerService(IProductService.class, mockService);
        IProductService retrievedService = factory.getService(IProductService.class);

        // Assert
        assertSame(mockService, retrievedService);
    }

    @Test
    void testMultipleServiceInstances_ShouldBeIndependent() {
        // Arrange
        ServiceFactory factory1 = ServiceFactory.getInstance();
        ServiceFactory factory2 = ServiceFactory.getInstance();

        // Act
        IProductService service1 = factory1.getService(IProductService.class);
        IProductService service2 = factory2.getService(IProductService.class);

        // Assert
        assertNotNull(service1);
        assertNotNull(service2);
        // Both should be the same instance due to singleton pattern
        assertSame(service1, service2);
    }

    @Test
    void testServiceDependencies_ShouldBeProperlyInjected() {
        // Arrange
        ServiceFactory factory = ServiceFactory.getInstance();

        // Act
        IProductService productService = factory.getService(IProductService.class);
        IConsumptionService consumptionService = factory.getService(IConsumptionService.class);
        IPurchaseOrderService purchaseOrderService = factory.getService(IPurchaseOrderService.class);

        // Assert
        assertNotNull(productService);
        assertNotNull(consumptionService);
        assertNotNull(purchaseOrderService);
        
        // Verify that services are properly instantiated (no null pointer exceptions)
        assertDoesNotThrow(() -> productService.getAllProducts());
    }
} 