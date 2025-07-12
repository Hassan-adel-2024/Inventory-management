package com.inventoryapp.inventorymanagement.ui;

import com.inventoryapp.inventorymanagement.dto.ConsumptionResponseDto;
import com.inventoryapp.inventorymanagement.model.Product;
import com.inventoryapp.inventorymanagement.service.impl.ConsumptionService;
import com.inventoryapp.inventorymanagement.service.impl.ProductService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class ProductConsumptionComponent {
    private final ProductService productService;
    private final ConsumptionService consumptionService;
    private final BiConsumer<String, Boolean> messageCallback;

    private final VBox container;
    private final TableView<ProductEntry> consumeTable;
    private final ObservableList<ProductEntry> entries;

    public ProductConsumptionComponent(ProductService productService,
                                       ConsumptionService consumptionService,
                                       BiConsumer<String, Boolean> messageCallback) {
        this.productService = productService;
        this.consumptionService = consumptionService;
        this.messageCallback = messageCallback;

        this.container = new VBox(10);
        this.container.setPadding(new Insets(10));

        this.entries = FXCollections.observableArrayList();
        this.consumeTable = buildConsumptionTable();

        Label titleLabel = new Label("Product Consumption");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox buttonBox = new HBox(10);
        Button refreshBtn = new Button("Refresh");
        Button clearAllBtn = new Button("Clear All Quantities");
        Button checkoutBtn = new Button("Checkout");
        checkoutBtn.setDisable(true); // Initially disabled

        // Listen for changes in any quantity to enable/disable checkout button
        entries.addListener((javafx.collections.ListChangeListener<ProductEntry>) change -> {
            updateCheckoutButtonState(checkoutBtn);
            for (ProductEntry entry : entries) {
                entry.quantityToConsume.addListener((obs,
                                                     oldVal, newVal) -> updateCheckoutButtonState(checkoutBtn));
            }
        });

        // Also update state after refresh
        refreshBtn.setOnAction(e -> {
            refreshData();
            updateCheckoutButtonState(checkoutBtn);
        });

        clearAllBtn.setOnAction(e -> {
            clearAllQuantities();
            updateCheckoutButtonState(checkoutBtn);
        });

        checkoutBtn.setOnAction(e -> checkout());

        buttonBox.getChildren().addAll(refreshBtn, clearAllBtn, checkoutBtn);

        container.getChildren().addAll(titleLabel, buttonBox, consumeTable);

        // Add RestockOrderComponent button below the table
        RestockOrderComponent restockOrderComponent = new RestockOrderComponent();
        container.getChildren().add(restockOrderComponent.getView());

        refreshData(); // Initial load
        updateCheckoutButtonState(checkoutBtn);
    }

    private void updateCheckoutButtonState(Button checkoutBtn) {
        boolean anySelected = entries.stream().anyMatch(e -> e.quantityToConsume.get() > 0);
        checkoutBtn.setDisable(!anySelected);
    }

    private TableView<ProductEntry> buildConsumptionTable() {
        TableView<ProductEntry> table = new TableView<>(entries);

        TableColumn<ProductEntry, Number> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().product.getProductId()));
        idCol.setPrefWidth(50);

        TableColumn<ProductEntry, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().product.getName()));
        nameCol.setPrefWidth(200);

        TableColumn<ProductEntry, Number> stockCol = new TableColumn<>("In Stock");
        stockCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().product.getCurrentStock()));
        stockCol.setPrefWidth(80);

        TableColumn<ProductEntry, Number> priceCol = new TableColumn<>("Unit Price");
        priceCol.setCellValueFactory(data -> new SimpleIntegerProperty((int) data.getValue().product.getUnitPrice()));
        priceCol.setPrefWidth(80);

        TableColumn<ProductEntry, Number> qtyCol = new TableColumn<>("Quantity to Consume");
        qtyCol.setCellValueFactory(data -> data.getValue().quantityToConsume);
        qtyCol.setCellFactory(getQuantityCellFactory());
        qtyCol.setPrefWidth(150);

        TableColumn<ProductEntry, String> messageCol = new TableColumn<>("Status");
        messageCol.setCellValueFactory(data -> data.getValue().statusMessage);
        messageCol.setPrefWidth(200);

        table.setRowFactory(tv -> new TableRow<ProductEntry>() {
            @Override
            protected void updateItem(ProductEntry entry, boolean empty) {
                super.updateItem(entry, empty);
                if (entry == null || empty) {
                    setStyle("");
                } else {
                    if (entry.product.getCurrentStock() == 0) {
                        setStyle("-fx-background-color: #ffcccc; -fx-opacity: 0.7;");
                    } else if (entry.quantityToConsume.get() > entry.product.getCurrentStock()) {
                        setStyle("-fx-background-color: #ffe6cc;");
                    } else if (entry.quantityToConsume.get() > 0) {
                        setStyle("-fx-background-color: #e6f3ff;");
                    } else {
                        setStyle("");
                    }
                }
            }
        });

        table.getColumns().addAll(idCol, nameCol, stockCol, priceCol, qtyCol, messageCol);
        table.setPrefHeight(400);

        return table;
    }

    private Callback<TableColumn<ProductEntry, Number>, TableCell<ProductEntry, Number>> getQuantityCellFactory() {
        return col -> new TableCell<ProductEntry, Number>() {
            private final Spinner<Integer> spinner = new Spinner<>(0, Integer.MAX_VALUE, 0);

            {
                spinner.setMaxWidth(80);
                spinner.setEditable(true); // Allow direct user input

                // Accept user input in spinner
                spinner.getEditor().textProperty().addListener((obs, oldText, newText) -> {
                    try {
                        int value = Integer.parseInt(newText);
                        spinner.getValueFactory().setValue(Math.max(0, value));
                    } catch (NumberFormatException ignored) {}
                });

                spinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                    if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                        ProductEntry entry = getTableView().getItems().get(getIndex());
                        int requestedQty = newValue != null ? newValue : 0;
                        entry.quantityToConsume.set(Math.max(0, requestedQty));
                        updateStatusMessage(entry, requestedQty);
                        getTableView().refresh();
                    }
                });
            }

            private void updateStatusMessage(ProductEntry entry, int requestedQty) {
                if (requestedQty == 0) {
                    entry.statusMessage.set("");
                } else if (entry.product.getCurrentStock() == 0) {
                    entry.statusMessage.set("❌ Out of stock");
                } else if (requestedQty > entry.product.getCurrentStock()) {
                    entry.statusMessage.set("⚠ Only " + entry.product.getCurrentStock() + " available");
                } else {
                    entry.statusMessage.set("✓ Available");
                }
            }

            @Override
            protected void updateItem(Number value, boolean empty) {
                super.updateItem(value, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    ProductEntry entry = getTableView().getItems().get(getIndex());
                    spinner.setDisable(entry.product.getCurrentStock() == 0);

                    spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                            0,
                            Math.max(entry.product.getCurrentStock() + 10, 1),
                            value != null ? value.intValue() : 0
                    ));

                    setGraphic(spinner);
                }
            }
        };
    }

    private void clearAllQuantities() {
        for (ProductEntry entry : entries) {
            entry.quantityToConsume.set(0);
            entry.statusMessage.set("");
        }
        consumeTable.refresh();
        messageCallback.accept("All quantities cleared", true);
    }

    private void checkout() {
        List<Pair<Integer, Integer>> validPairs = new ArrayList<>();
        List<String> issues = new ArrayList<>();
        List<String> insufficientProducts = new ArrayList<>();

        for (ProductEntry entry : entries) {
            int available = entry.product.getCurrentStock();
            int requested = entry.quantityToConsume.get();

            if (requested > 0) {
                if (available >= requested) {
                    validPairs.add(new Pair<>(entry.product.getProductId(), requested));
                } else {
                    issues.add("Only " + available + " in stock for '" + entry.product.getName() + "'");
                    insufficientProducts.add(entry.product.getName());
                }
            }
        }

        if (!insufficientProducts.isEmpty()) {
            javafx.application.Platform.runLater(() -> {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Insufficient Stock");
                confirmAlert.setHeaderText("Some products have insufficient stock:");
                confirmAlert.setContentText(
                    String.join("\n", issues) +
                    "\n\nDo you want to continue checkout without these products?"
                );
                ButtonType yesBtn = new ButtonType("Continue", ButtonBar.ButtonData.YES);
                ButtonType noBtn = new ButtonType("Cancel", ButtonBar.ButtonData.NO);
                confirmAlert.getButtonTypes().setAll(yesBtn, noBtn);

                confirmAlert.showAndWait().ifPresent(result -> {
                    if (result == yesBtn) {
                        proceedCheckout(validPairs, issues);
                    } else {
                        messageCallback.accept("Checkout cancelled due to insufficient stock.", false);
                    }
                });
            });
        } else {
            proceedCheckout(validPairs, issues);
        }
    }

    private void proceedCheckout(List<Pair<Integer, Integer>> validPairs, List<String> issues) {
        StringBuilder messageBuilder = new StringBuilder();
        boolean overallSuccess = true;

        if (!validPairs.isEmpty()) {
            ConsumptionResponseDto response = consumptionService.consumeProduct(validPairs);
            messageBuilder.append(response.getMessage());
            if (!response.isSuccess()) {
                overallSuccess = false;
            }
        }

        if (!issues.isEmpty()) {
            if (messageBuilder.length() > 0) {
                messageBuilder.append("\n");
            }
            messageBuilder.append("Issues:\n");
            for (String issue : issues) {
                messageBuilder.append("• ").append(issue).append("\n");
            }
            overallSuccess = false;
        }

        if (validPairs.isEmpty() && issues.isEmpty()) {
            messageBuilder.append("No products selected for consumption");
        }

        messageCallback.accept(messageBuilder.toString().trim(), overallSuccess);

        refreshData();

        // --- Automatic Purchase Order Logic ---
        // (Removed: now handled by RestockOrderComponent)
    }

    public void refreshData() {
        entries.clear();
        entries.addAll(
                productService.getAllProducts().stream()
                        .map(ProductEntry::new)
                        .collect(Collectors.toList())
        );

        consumeTable.setItems(entries);
        consumeTable.refresh();
    }

    public Node getView() {
        return container;
    }

    public static class ProductEntry {
        public final Product product;
        public final SimpleIntegerProperty quantityToConsume = new SimpleIntegerProperty(0);
        public final SimpleStringProperty statusMessage = new SimpleStringProperty("");

        public ProductEntry(Product product) {
            this.product = product;
        }
    }

}
