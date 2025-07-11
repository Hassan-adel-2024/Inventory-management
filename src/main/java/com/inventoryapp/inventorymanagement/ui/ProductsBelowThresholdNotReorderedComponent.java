package com.inventoryapp.inventorymanagement.ui;

import com.inventoryapp.inventorymanagement.dao.ProductDao;
import com.inventoryapp.inventorymanagement.dao.PurchaseOrderDao;
import com.inventoryapp.inventorymanagement.dao.PurchaseOrderItemDao;
import com.inventoryapp.inventorymanagement.model.Product;
import com.inventoryapp.inventorymanagement.model.PurchaseOrder;
import com.inventoryapp.inventorymanagement.model.PurchaseOrderItem;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class ProductsBelowThresholdNotReorderedComponent {
    private final VBox container = new VBox(10);

    public ProductsBelowThresholdNotReorderedComponent() {
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

        List<ProductRow> rows = new ArrayList<>();
        try {
            ProductDao productDao = new ProductDao();
            PurchaseOrderDao orderDao = new PurchaseOrderDao();
            PurchaseOrderItemDao itemDao = new PurchaseOrderItemDao();

            List<Product> products = productDao.findAll();
            List<PurchaseOrder> orders = orderDao.findAll();
            List<PurchaseOrderItem> items = itemDao.findAll();

            Set<Integer> productsWithUndeliveredOrder = items.stream()
                    .filter(item -> {
                        PurchaseOrder order = orders.stream()
                                .filter(o -> o.getOrderID() == item.getOrderID())
                                .findFirst().orElse(null);
                        return order != null && !order.isDelivered();
                    })
                    .map(PurchaseOrderItem::getProductID)
                    .collect(Collectors.toSet());

            for (Product product : products) {
                // Show products with stock < threshold, including zero stock, and not yet reordered
                if (product.getCurrentStock() < product.getReorderThreshold()
                        && !productsWithUndeliveredOrder.contains(product.getProductId())) {
                    rows.add(new ProductRow(
                            product.getProductId(),
                            product.getName(),
                            product.getCurrentStock(),
                            product.getReorderThreshold()
                    ));
                }
            }
        } catch (Exception e) {
            // Handle error, optionally show in UI
        }

        table.getItems().addAll(rows);
        container.getChildren().add(table);
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
