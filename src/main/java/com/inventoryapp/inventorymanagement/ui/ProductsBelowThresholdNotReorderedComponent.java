package com.inventoryapp.inventorymanagement.ui;

import com.inventoryapp.inventorymanagement.dao.impl.ProductDaoImpl;
import com.inventoryapp.inventorymanagement.dao.impl.PurchaseOrderDaoImpl;
import com.inventoryapp.inventorymanagement.dao.impl.PurchaseOrderItemDaoImpl;
import com.inventoryapp.inventorymanagement.model.Product;
import com.inventoryapp.inventorymanagement.model.PurchaseOrder;
import com.inventoryapp.inventorymanagement.model.PurchaseOrderItem;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.stream.Collectors;

public class ProductsBelowThresholdNotReorderedComponent {
    private final VBox container = new VBox(10);

    public ProductsBelowThresholdNotReorderedComponent() {
        TableView<ProductRow> table = createTable();
        try {
            List<ProductRow> rows = loadProductData();
            table.getItems().addAll(rows);
        } catch (Exception e) {
            e.printStackTrace(); // Don't silently swallow exceptions
        }
        container.getChildren().add(table);
    }

    private TableView<ProductRow> createTable() {
        TableView<ProductRow> table = new TableView<>();
        table.setPrefHeight(400);

        TableColumn<ProductRow, Integer> idCol = new TableColumn<>("Product ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("productId"));

        TableColumn<ProductRow, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ProductRow, Integer> stockCol = new TableColumn<>("Current Stock");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("currentStock"));

        TableColumn<ProductRow, Integer> thresholdCol = new TableColumn<>("Threshold");
        thresholdCol.setCellValueFactory(new PropertyValueFactory<>("threshold"));

        table.getColumns().addAll(idCol, nameCol, stockCol, thresholdCol);
        return table;
    }

    private List<ProductRow> loadProductData() throws Exception {
        ProductDaoImpl productDaoImpl = new ProductDaoImpl();
        PurchaseOrderDaoImpl orderDao = new PurchaseOrderDaoImpl();
        PurchaseOrderItemDaoImpl itemDao = new PurchaseOrderItemDaoImpl();

        List<Product> products = productDaoImpl.findAll();
        List<PurchaseOrder> allOrders = orderDao.findAll();
        List<PurchaseOrderItem> allItems = itemDao.findAll();

        // Get all active (not delivered and not deleted) order items
        Set<Integer> productsWithActiveOrders = allItems.stream()
                .filter(item -> {
                    PurchaseOrder order = findOrderById(allOrders, item.getOrderID());
                    return order != null && !order.isDelivered() && !order.isDeleted();
                })
                .map(PurchaseOrderItem::getProductID)
                .collect(Collectors.toSet());

        return products.stream()
                .filter(product -> product.getCurrentStock() < product.getReorderThreshold())
                .filter(product -> !productsWithActiveOrders.contains(product.getProductId()))
                .map(product -> new ProductRow(
                        product.getProductId(),
                        product.getName(),
                        product.getCurrentStock(),
                        product.getReorderThreshold()
                ))
                .collect(Collectors.toList());
    }

    private PurchaseOrder findOrderById(List<PurchaseOrder> orders, int orderId) {
        return orders.stream()
                .filter(o -> o.getOrderID() == orderId)
                .findFirst()
                .orElse(null);
    }

    public Node getView() {
        return container;
    }

    public static class ProductRow {
        private final int productId;
        private final String name;
        private final int currentStock;
        private final int threshold;

        public ProductRow(int productId, String name, int currentStock, int threshold) {
            this.productId = productId;
            this.name = name;
            this.currentStock = currentStock;
            this.threshold = threshold;
        }

        public int getProductId() { return productId; }
        public String getName() { return name; }
        public int getCurrentStock() { return currentStock; }
        public int getThreshold() { return threshold; }
    }
}