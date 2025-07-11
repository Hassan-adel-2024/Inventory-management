package com.inventoryapp.inventorymanagement.service.impl;

import com.inventoryapp.inventorymanagement.dao.ProductDao;
import com.inventoryapp.inventorymanagement.service.IProductService;
import com.inventoryapp.inventorymanagement.service.IConsumptionService;
import com.inventoryapp.inventorymanagement.service.IPurchaseOrderService;
import com.inventoryapp.inventorymanagement.service.ISupplierService;

public class ServiceFactory {
    private static ProductDao productDao;
    private static ProductService productService;
    private static ConsumptionService consumptionService;
    private static PurchaseOrderService purchaseOrderService;
    private static SupplierService supplierService;

    public static synchronized ProductDao getProductDao() {
        if (productDao == null) {
            productDao = new ProductDao();
        }
        return productDao;
    }

    public static synchronized IProductService getProductService() {
        if (productService == null) {
//            productService = new ProductService(getProductDao());
        }
        return productService;
    }

    public static synchronized IConsumptionService getConsumptionService() {
        if (consumptionService == null) {
            consumptionService = new ConsumptionService((ProductService) getProductService());
        }
        return consumptionService;
    }

    public static synchronized IPurchaseOrderService getPurchaseOrderService() {
        if (purchaseOrderService == null) {
            purchaseOrderService = new PurchaseOrderService();
        }
        return purchaseOrderService;
    }

    public static synchronized ISupplierService getSupplierService() {
        if (supplierService == null) {
            supplierService = new SupplierService();
        }
        return supplierService;
    }
}
