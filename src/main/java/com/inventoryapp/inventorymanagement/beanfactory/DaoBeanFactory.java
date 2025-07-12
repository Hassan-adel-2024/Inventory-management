package com.inventoryapp.inventorymanagement.beanfactory;

import com.inventoryapp.inventorymanagement.dao.*;
import com.inventoryapp.inventorymanagement.dao.impl.ProductDaoImpl;
import com.inventoryapp.inventorymanagement.dao.impl.PurchaseOrderDaoImpl;
import com.inventoryapp.inventorymanagement.dao.impl.PurchaseOrderItemDaoImpl;
import com.inventoryapp.inventorymanagement.dao.impl.SupplierDaoImpl;

import java.util.HashMap;
import java.util.Map;

public class DaoBeanFactory {
    private static volatile DaoBeanFactory instance;
    private final Map<Class<?>, Object> daoRegistry = new HashMap<>();

    private DaoBeanFactory() {
        // Initialize all DAO implementations
        ProductDao productDao = new ProductDaoImpl();
        daoRegistry.put(ProductDao.class, productDao);
        daoRegistry.put(BaseDao.class, productDao); // Also register as BaseDao<Product>

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
