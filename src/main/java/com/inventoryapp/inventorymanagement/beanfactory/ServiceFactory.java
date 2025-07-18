package com.inventoryapp.inventorymanagement.beanfactory;

import com.inventoryapp.inventorymanagement.dao.ProductDao;
import com.inventoryapp.inventorymanagement.dao.PurchaseOrderDao;
import com.inventoryapp.inventorymanagement.dao.PurchaseOrderItemDao;
import com.inventoryapp.inventorymanagement.dao.SupplierDao;
import com.inventoryapp.inventorymanagement.service.IConsumptionService;
import com.inventoryapp.inventorymanagement.service.IProductService;
import com.inventoryapp.inventorymanagement.service.IPurchaseOrderService;
import com.inventoryapp.inventorymanagement.service.ISupplierService;
import com.inventoryapp.inventorymanagement.service.impl.ConsumptionService;
import com.inventoryapp.inventorymanagement.service.impl.ProductService;
import com.inventoryapp.inventorymanagement.service.impl.PurchaseOrderService;
import com.inventoryapp.inventorymanagement.service.impl.SupplierService;

import java.util.HashMap;
import java.util.Map;

public class ServiceFactory {
    /**
     * Singleton instance of DaoBeanFactory
     * volatile ensures that multiple threads can handle the instance safely
     */
    private static volatile ServiceFactory instance;
    private final Map<Class<?>, Object> serviceRegistry = new HashMap<>();
    private final DaoBeanFactory daoFactory = DaoBeanFactory.getInstance();

    private ServiceFactory() {
        // Initialize DAOs first
        ProductDao productDao = daoFactory.getDao(ProductDao.class);
        PurchaseOrderDao purchaseOrderDao = daoFactory.getDao(PurchaseOrderDao.class);
        PurchaseOrderItemDao purchaseOrderItemDao = daoFactory.getDao(PurchaseOrderItemDao.class);
        SupplierDao supplierDao = daoFactory.getDao(SupplierDao.class);

        // Initialize all service implementations with proper dependencies
        IProductService productService = new ProductService(productDao);
        serviceRegistry.put(IProductService.class, productService);

        IPurchaseOrderService purchaseOrderService = new PurchaseOrderService(
                purchaseOrderDao,
                purchaseOrderItemDao,
                productService
        );
        serviceRegistry.put(IPurchaseOrderService.class, purchaseOrderService);

        ISupplierService supplierService = new SupplierService();
        serviceRegistry.put(ISupplierService.class, supplierService);

        IConsumptionService consumptionService = new ConsumptionService(productService);
        serviceRegistry.put(IConsumptionService.class, consumptionService);
    }

    public static ServiceFactory getInstance() {
        if (instance == null) {
            synchronized (ServiceFactory.class) {
                if (instance == null) {
                    instance = new ServiceFactory();
                }
            }
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceInterface) {
        Object service = serviceRegistry.get(serviceInterface);
        if (service == null) {
            throw new IllegalArgumentException("No service registered for interface: " + serviceInterface.getName());
        }
        return (T) service;
    }

    // Optional: For testing purposes
    public synchronized <T> void registerService(Class<T> serviceInterface, T implementation) {
        serviceRegistry.put(serviceInterface, implementation);
    }
}
