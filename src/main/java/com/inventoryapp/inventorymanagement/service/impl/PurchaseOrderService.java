package com.inventoryapp.inventorymanagement.service.impl;

import com.inventoryapp.inventorymanagement.dao.ProductDao;
import com.inventoryapp.inventorymanagement.dao.PurchaseOrderDao;
import com.inventoryapp.inventorymanagement.dao.PurchaseOrderItemDao;
import com.inventoryapp.inventorymanagement.model.Product;
import com.inventoryapp.inventorymanagement.model.PurchaseOrder;
import com.inventoryapp.inventorymanagement.model.PurchaseOrderItem;
import com.inventoryapp.inventorymanagement.service.IPurchaseOrderService;
import com.inventoryapp.inventorymanagement.service.IProductService;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PurchaseOrderService implements IPurchaseOrderService {

    private final PurchaseOrderDao purchaseOrderDao;
    private final PurchaseOrderItemDao purchaseOrderItemDao;
    private final IProductService productService;

    public PurchaseOrderService(PurchaseOrderDao purchaseOrderDao,
                                PurchaseOrderItemDao purchaseOrderItemDao,
                                IProductService productService) {
        this.purchaseOrderDao = purchaseOrderDao;
        this.purchaseOrderItemDao = purchaseOrderItemDao;
        this.productService = productService;
    }

    public List<String> createPurchaseOrdersForLowStockProducts() throws SQLException {
        List<String> notifications = new ArrayList<>();

        List<Product> products = productService.getProductsBelowThreshold();

        for (Product product : products) {
            int threshold = product.getReorderThreshold();
            int currentStock = product.getCurrentStock();
            int maxOrderAmount = threshold * 2 - currentStock;

            List<PurchaseOrderItem> allItems = purchaseOrderItemDao.findAll();
            PurchaseOrderItem undeliveredItem = null;
            PurchaseOrder undeliveredOrder = null;

            for (PurchaseOrderItem item : allItems) {
                if (item.getProductID() == product.getProductId()) {
                    PurchaseOrder order = purchaseOrderDao.findById(item.getOrderID());
                    if (order != null && !order.isDelivered() && !order.isDeleted()) {
                        undeliveredItem = item;
                        undeliveredOrder = order;
                        break;
                    }
                }
            }

            if (undeliveredItem == null) {
                PurchaseOrder order = new PurchaseOrder();
                order.setSupplierID(product.getSupplierId());
                order.setCreatedAt(new Date());
                order.setDelivered(false);
                purchaseOrderDao.save(order);

                PurchaseOrderItem item = new PurchaseOrderItem();
                item.setOrderID(order.getOrderID());
                item.setProductID(product.getProductId());
                item.setUnitPrice(product.getUnitPrice());
                item.setQuantity(maxOrderAmount);
                purchaseOrderItemDao.save(item);

                notifications.add("Start order for " + product.getName() + " (Qty: " + maxOrderAmount + ")");
            } else {
                int oldQty = undeliveredItem.getQuantity();
                int newQty = Math.max(0, maxOrderAmount - oldQty);
                int finalQty = Math.min(oldQty + newQty, threshold * 2 - currentStock);

                if (finalQty > oldQty) {
                    undeliveredItem.setQuantity(finalQty);
                    purchaseOrderItemDao.update(undeliveredItem);
                    notifications.add("Follow-up modification for " + product.getName() +
                            " (Old Qty: " + oldQty + ", New Qty: " + finalQty + ")");
                } else {
                    notifications.add("No modification needed for " + product.getName() +
                            " (Qty already at limit)");
                }
            }
        }

        return notifications;
    }

    public boolean markOrderAsDelivered(int orderId) throws SQLException {
        PurchaseOrder order = purchaseOrderDao.findById(orderId);
        if (order == null || order.isDelivered() || order.isDeleted()) {
            return false;
        }

        List<PurchaseOrderItem> items = purchaseOrderItemDao.findAll();
        for (PurchaseOrderItem item : items) {
            if (item.getOrderID() == orderId) {
                Product product = productService.getProductById(item.getProductID());
                if (product != null) {
                    int newStock = product.getCurrentStock() + item.getQuantity();
                    product.setCurrentStock(newStock);
                    productService.updateProduct(product);
                }
            }
        }

        order.setDelivered(true);
        purchaseOrderDao.update(order);
        return true;
    }

    public boolean markOrderAsDeleted(int orderId) throws SQLException {
        PurchaseOrder order = purchaseOrderDao.findById(orderId);
        if (order == null || order.isDeleted() || order.isDelivered()) {
            return false;
        }
        order.setDeleted(true);
        purchaseOrderDao.update(order);
        return true;
    }
}
