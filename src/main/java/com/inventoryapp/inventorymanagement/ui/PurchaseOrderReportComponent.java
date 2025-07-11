package com.inventoryapp.inventorymanagement.ui;

// ...existing imports...

import com.inventoryapp.inventorymanagement.dao.ProductDao;
import com.inventoryapp.inventorymanagement.dao.PurchaseOrderDao;
import com.inventoryapp.inventorymanagement.dao.PurchaseOrderItemDao;
import com.inventoryapp.inventorymanagement.dao.SupplierDao;
import com.inventoryapp.inventorymanagement.model.Product;
import com.inventoryapp.inventorymanagement.model.PurchaseOrder;
import com.inventoryapp.inventorymanagement.model.PurchaseOrderItem;
import com.inventoryapp.inventorymanagement.model.Supplier;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.inventoryapp.inventorymanagement.ui.ReportRow;

public class PurchaseOrderReportComponent {
    private final VBox container = new VBox(10);

    public PurchaseOrderReportComponent() {
        TableView<ReportRow> table = new TableView<>();
        table.setPrefHeight(400);

        TableColumn<ReportRow, Integer> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        TableColumn<ReportRow, String> supplierCol = new TableColumn<>("Supplier");
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplierName"));

        TableColumn<ReportRow, String> productCol = new TableColumn<>("Product");
        productCol.setCellValueFactory(new PropertyValueFactory<>("productName"));

        TableColumn<ReportRow, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<ReportRow, Double> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        TableColumn<ReportRow, Boolean> deliveredCol = new TableColumn<>("Delivered");
        deliveredCol.setCellValueFactory(new PropertyValueFactory<>("delivered"));

        table.getColumns().addAll(orderIdCol, supplierCol, productCol, quantityCol, priceCol, deliveredCol);

        List<ReportRow> rows = new ArrayList<>();
        try {
            PurchaseOrderDao orderDao = new PurchaseOrderDao();
            PurchaseOrderItemDao itemDao = new PurchaseOrderItemDao();
            ProductDao productDao = new ProductDao();
            SupplierDao supplierDao = new SupplierDao();

            List<PurchaseOrder> orders = orderDao.findAll();
            List<PurchaseOrderItem> items = itemDao.findAll();

            Map<Integer, Product> productMap = new HashMap<>();
            for (Product p : productDao.findAll()) productMap.put(p.getProductId(), p);

            Map<Integer, Supplier> supplierMap = new HashMap<>();
            for (Supplier s : supplierDao.findAll()) supplierMap.put(s.getSupplierId(), s);

            for (PurchaseOrderItem item : items) {
                PurchaseOrder order = orders.stream()
                        .filter(o -> o.getOrderID() == item.getOrderID())
                        .findFirst().orElse(null);
                if (order == null) continue;
                Product product = productMap.get(item.getProductID());
                Supplier supplier = supplierMap.get(order.getSupplierID());
                rows.add(new ReportRow(
                        order.getOrderID(),
                        supplier != null ? supplier.getName() : "Unknown",
                        product != null ? product.getName() : "Unknown",
                        item.getQuantity(),
                        item.getUnitPrice(),
                        order.isDelivered()
                ));
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
}
