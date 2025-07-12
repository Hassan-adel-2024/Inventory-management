package com.inventoryapp.inventorymanagement.service;

import java.sql.SQLException;
import java.util.List;

public interface IPurchaseOrderService {

public boolean markOrderAsDeleted(int orderId) throws SQLException;
    List<String> createPurchaseOrdersForLowStockProducts() throws SQLException;
    boolean markOrderAsDelivered(int orderId) throws SQLException;

}

