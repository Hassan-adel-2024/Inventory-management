package com.inventoryapp.inventorymanagement.service.impl;

import com.inventoryapp.inventorymanagement.dao.ProductDao;
import com.inventoryapp.inventorymanagement.dao.PurchaseOrderDao;
import com.inventoryapp.inventorymanagement.dao.PurchaseOrderItemDao;
import com.inventoryapp.inventorymanagement.model.Product;
import com.inventoryapp.inventorymanagement.model.PurchaseOrder;
import com.inventoryapp.inventorymanagement.model.PurchaseOrderItem;
import com.inventoryapp.inventorymanagement.service.IPurchaseOrderService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PurchaseOrderService implements IPurchaseOrderService {

    public List<String> createPurchaseOrdersForLowStockProducts() {
        List<String> notifications = new ArrayList<>();
        try {
            ProductService productService = new ProductService();
            PurchaseOrderDao purchaseOrderDao = new PurchaseOrderDao();
            PurchaseOrderItemDao purchaseOrderItemDao = new PurchaseOrderItemDao();

            List<Product> products = productService.getProductsBelowThreshold();

            for (Product product : products) {
                int threshold = product.getReorderThreshold();
                int currentStock = product.getCurrentStock();
                int maxOrderAmount = threshold * 2 - currentStock;

                // Check for undelivered order
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
                    // No undelivered order, create new order and item
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
                    // Undelivered order exists, update quantity
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
        } catch (Exception e) {
            notifications.add("Failed to place/modify automatic purchase order: " + e.getMessage());
        }
        return notifications;
    }

    /**
     * Marks the given purchase order as delivered and updates product stock.
     * @param orderId The ID of the order to mark as delivered.
     * @return true if successful, false otherwise.
     */
    public boolean markOrderAsDelivered(int orderId) {
        try {
            PurchaseOrderDao purchaseOrderDao = new PurchaseOrderDao();
            PurchaseOrderItemDao purchaseOrderItemDao = new PurchaseOrderItemDao();
            ProductService productService = new ProductService();

            PurchaseOrder order = purchaseOrderDao.findById(orderId);
            if (order == null || order.isDelivered() || order.isDeleted()) {
                return false;
            }

            // Update product stock for each item in the order
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
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Marks the given purchase order as deleted.
     * @param orderId The ID of the order to mark as deleted.
     * @return true if successful, false otherwise.
     */
    public boolean markOrderAsDeleted(int orderId) {
        try {
            PurchaseOrderDao purchaseOrderDao = new PurchaseOrderDao();
            PurchaseOrder order = purchaseOrderDao.findById(orderId);
            // Prevent deletion if delivered
            if (order == null || order.isDeleted() || order.isDelivered()) {
                return false;
            }
            order.setDeleted(true);
            purchaseOrderDao.update(order);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

