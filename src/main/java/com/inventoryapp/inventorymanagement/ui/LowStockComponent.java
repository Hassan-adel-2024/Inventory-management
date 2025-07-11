package com.inventoryapp.inventorymanagement.ui;
import com.inventoryapp.inventorymanagement.model.Product;
import com.inventoryapp.inventorymanagement.service.impl.ProductService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
public class LowStockComponent {
    private final ProductService productService;
    private final VBox container;
    private final TableView<Product> lowStockTable;
    private final Label statusLabel;

    public LowStockComponent(ProductService productService) {
        this.productService = productService;
        this.container = new VBox(10);
        this.container.setPadding(new Insets(10));

        this.statusLabel = new Label();
        this.statusLabel.setTextFill(Color.DARKBLUE);

        this.lowStockTable = buildProductTable();

        Label titleLabel = new Label("Low Stock Products");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setOnAction(e -> refreshData());

        container.getChildren().addAll(titleLabel, refreshBtn, statusLabel, lowStockTable);

        // Initial data load
        refreshData();
    }
    private TableView<Product> buildProductTable() {
        TableView<Product> table = new TableView<>();

        TableColumn<Product, Number> idCol = new TableColumn<>("Product ID");
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getProductId()));
        idCol.setPrefWidth(80);

        TableColumn<Product, String> nameCol = new TableColumn<>("Product Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        nameCol.setPrefWidth(200);

        TableColumn<Product, Number> stockCol = new TableColumn<>("Current Stock");
        stockCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCurrentStock()));
        stockCol.setPrefWidth(100);

        TableColumn<Product, Number> thresholdCol = new TableColumn<>("Reorder Threshold");
        thresholdCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getReorderThreshold()));
        thresholdCol.setPrefWidth(120);

        TableColumn<Product, String> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(data -> new SimpleStringProperty(
                String.format("$%.2f", data.getValue().getUnitPrice())
        ));
        priceCol.setPrefWidth(80);

        // Add row factory to highlight critical stock levels
        table.setRowFactory(tv -> new TableRow<Product>() {
            @Override
            protected void updateItem(Product product, boolean empty) {
                super.updateItem(product, empty);
                if (product == null || empty) {
                    setStyle("");
                } else {
                    if (product.getCurrentStock() == 0) {
                        setStyle("-fx-background-color: #ffcccc;"); // Red for out of stock
                    } else if (product.getCurrentStock() <= product.getReorderThreshold() / 2) {
                        setStyle("-fx-background-color: #ffe6cc;"); // Orange for critically low
                    } else {
                        setStyle("-fx-background-color: #fff2cc;"); // Yellow for low stock
                    }
                }
            }
        });

        table.getColumns().addAll(idCol, nameCol, stockCol, thresholdCol, priceCol);
        table.setPrefHeight(300);

        return table;
    }

    public void refreshData() {
        ObservableList<Product> lowStockProducts = FXCollections.observableArrayList(
                productService.getProductsBelowThreshold()
        );

        lowStockTable.setItems(lowStockProducts);

        // Update status label
        if (lowStockProducts.isEmpty()) {
            statusLabel.setText("✓ All products are above reorder threshold");
            statusLabel.setTextFill(Color.DARKGREEN);
        } else {
            long outOfStock = lowStockProducts.stream()
                    .filter(p -> p.getCurrentStock() == 0)
                    .count();

            if (outOfStock > 0) {
                statusLabel.setText(String.format("⚠ %d products below threshold (%d out of stock)",
                        lowStockProducts.size(), outOfStock));
                statusLabel.setTextFill(Color.DARKRED);
            } else {
                statusLabel.setText(String.format("⚠ %d products below threshold",
                        lowStockProducts.size()));
                statusLabel.setTextFill(Color.DARKORANGE);
            }
        }
    }

    public Node getView() {
        return container;
    }
}

