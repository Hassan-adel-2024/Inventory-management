package com.inventoryapp.inventorymanagement.beanfactory;

import com.inventoryapp.inventorymanagement.dao.*;
import com.inventoryapp.inventorymanagement.dao.impl.ProductDaoImpl;
import com.inventoryapp.inventorymanagement.dao.impl.PurchaseOrderDaoImpl;
import com.inventoryapp.inventorymanagement.dao.impl.PurchaseOrderItemDaoImpl;
import com.inventoryapp.inventorymanagement.dao.impl.SupplierDaoImpl;

import java.util.HashMap;
import java.util.Map;

public class DaoBeanFactory {
    /**
     * Singleton instance of DaoBeanFactory
     * volatile ensures that multiple threads can handle the instance safely
     * prevents to be cached in cache by one thread before it is initialized by another
     */
    private static volatile DaoBeanFactory instance;
    /*
      using hashmap to store DAO implementations
      to make sure that we can retrieve them by their interface type to avoid tight coupling
      high level module should not depend on low level module (DI principle)
     */
    private final Map<Class<?>, Object> daoRegistry = new HashMap<>();

    private DaoBeanFactory() {
        // Initialize all DAO implementations
        // Registering DAOs with their respective interfaces
        // This allows us to retrieve them later without tight coupling

        ProductDao productDao = new ProductDaoImpl();
        daoRegistry.put(ProductDao.class, productDao);
        daoRegistry.put(BaseDao.class, productDao);

        PurchaseOrderDao orderDao = new PurchaseOrderDaoImpl();
        daoRegistry.put(PurchaseOrderDao.class, orderDao);

        PurchaseOrderItemDao itemDao = new PurchaseOrderItemDaoImpl();
        daoRegistry.put(PurchaseOrderItemDao.class, itemDao);

        SupplierDao supplierDao = new SupplierDaoImpl();
        daoRegistry.put(SupplierDao.class, supplierDao);
    }

    public static DaoBeanFactory getInstance() {
        if (instance == null) {
            synchronized (DaoBeanFactory.class) {
                if (instance == null) {
                    instance = new DaoBeanFactory();
                }
            }
        }
        return instance;
    }

    @SuppressWarnings("unchecked")
    public <T> T getDao(Class<T> daoInterface) {
        Object dao = daoRegistry.get(daoInterface);
        if (dao == null) {
            throw new IllegalArgumentException("No DAO registered for interface: " + daoInterface.getName());
        }
        return (T) dao;
    }

    public <T, D extends BaseDao<T>> D getBaseDao(Class<D> daoClass) {
        return getDao(daoClass);
    }
}
