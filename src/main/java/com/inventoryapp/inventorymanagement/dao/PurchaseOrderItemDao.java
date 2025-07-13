package com.inventoryapp.inventorymanagement.dao;

import com.inventoryapp.inventorymanagement.model.PurchaseOrderItem;

import java.sql.SQLException;
import java.util.List;

public interface PurchaseOrderItemDao extends BaseDao<PurchaseOrderItem>{
    List<PurchaseOrderItem> findByOrderId(int orderId) throws SQLException;
    List<PurchaseOrderItem> findUndeliveredItemsByProductId(int productId) throws SQLException;
    void updateQuantity(int orderItemId, int newQuantity) throws SQLException;
}
