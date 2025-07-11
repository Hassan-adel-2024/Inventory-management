package com.inventoryapp.inventorymanagement.ui;

import com.inventoryapp.inventorymanagement.model.Product;
import com.inventoryapp.inventorymanagement.service.impl.ProductService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProductTableViewBuilder {
    private final ProductService productService;

    public ProductTableViewBuilder(ProductService productService) {
        this.productService = productService;
    }

    public TableView<Product> buildProductTable() {
        TableView<Product> tableView = new TableView<>();

        TableColumn<Product, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("productId"));

        TableColumn<Product, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Product, Number> stockCol = new TableColumn<>("Stock");
        stockCol.setCellValueFactory(new PropertyValueFactory<>("currentStock"));

        TableColumn<Product, Number> thresholdCol = new TableColumn<>("Reorder Threshold");
        thresholdCol.setCellValueFactory(new PropertyValueFactory<>("reorderThreshold"));

        TableColumn<Product, Number> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));

        TableColumn<Product, Number> supplierCol = new TableColumn<>("Supplier ID");
        supplierCol.setCellValueFactory(new PropertyValueFactory<>("supplierId"));

        tableView.getColumns().addAll(idCol, nameCol, stockCol, thresholdCol, priceCol, supplierCol);

        ObservableList<Product> products = FXCollections.observableArrayList(productService.getProductsBelowThreshold());
        tableView.setItems(products);

        return tableView;
    }
}
