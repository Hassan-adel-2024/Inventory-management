package com.inventoryapp.inventorymanagement.dao;

import com.inventoryapp.inventorymanagement.model.PurchaseOrder;

import java.sql.SQLException;
import java.util.List;

public interface PurchaseOrderDao extends BaseDao<PurchaseOrder>{
    List<PurchaseOrder> findUndeliveredOrders() throws SQLException;
    void markAsDelivered(int orderId) throws SQLException;
    void markAsDeleted(int orderId) throws SQLException;
}
