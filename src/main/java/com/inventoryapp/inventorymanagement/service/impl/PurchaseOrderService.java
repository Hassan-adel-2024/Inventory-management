package com.inventoryapp.inventorymanagement.service.impl;

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
import java.util.logging.Logger;

public class PurchaseOrderService implements IPurchaseOrderService {
    private static final Logger logger = Logger.getLogger(PurchaseOrderService.class.getName());
    
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

            List<PurchaseOrderItem> undeliveredItems = purchaseOrderItemDao.findUndeliveredItemsByProductId(product.getProductId());

            if (undeliveredItems.isEmpty()) {
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
                logger.info("Created new purchase order for product: " + product.getName());
            } else {
                // Modify existing order
                PurchaseOrderItem undeliveredItem = undeliveredItems.get(0); // Take the first undelivered item
                int oldQty = undeliveredItem.getQuantity();
                int newQty = Math.max(0, maxOrderAmount - oldQty);
                int finalQty = Math.min(oldQty + newQty, threshold * 2 - currentStock);

                if (finalQty > oldQty) {
                    purchaseOrderItemDao.updateQuantity(undeliveredItem.getOrderItemID(), finalQty);
                    notifications.add("Follow-up modification for " + product.getName() +
                            " (Old Qty: " + oldQty + ", New Qty: " + finalQty + ")");
                    logger.info("Modified existing purchase order for product: " + product.getName());
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
            logger.warning("Cannot mark order as delivered: Order ID " + orderId + " not found or already processed");
            return false;
        }

        List<PurchaseOrderItem> items = purchaseOrderItemDao.findByOrderId(orderId);
        
        for (PurchaseOrderItem item : items) {
            Product product = productService.getProductById(item.getProductID());
            if (product != null) {
                int newStock = product.getCurrentStock() + item.getQuantity();
                product.setCurrentStock(newStock);
                productService.updateProduct(product);
                logger.info("Updated stock for product: " + product.getName() + " (+" + item.getQuantity() + ")");
            }
        }

        purchaseOrderDao.markAsDelivered(orderId);
        logger.info("Order marked as delivered: " + orderId);
        return true;
    }

    public boolean markOrderAsDeleted(int orderId) throws SQLException {
        PurchaseOrder order = purchaseOrderDao.findById(orderId);
        if (order == null || order.isDeleted() || order.isDelivered()) {
            logger.warning("Cannot mark order as deleted: Order ID " + orderId + " not found or already processed");
            return false;
        }
        
        purchaseOrderDao.markAsDeleted(orderId);
        logger.info("Order marked as deleted: " + orderId);
        return true;
    }
}
