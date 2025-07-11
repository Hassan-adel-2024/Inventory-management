package com.inventoryapp.inventorymanagement.ui;

import com.inventoryapp.inventorymanagement.dto.ConsumptionResponseDto;
import com.inventoryapp.inventorymanagement.model.Product;
import com.inventoryapp.inventorymanagement.service.impl.ConsumptionService;
import com.inventoryapp.inventorymanagement.service.impl.ProductService;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.Arrays;

public class MainView {
    private final ProductService productService = new ProductService();
    private final ConsumptionService consumptionService = new ConsumptionService(productService);

    // ✅ Refactored to use proper type
    private TableView<Product> productTable;
    private final Label resultLabel = new Label();

    public Node buildMainUI() {
        VBox root = new VBox(10);
        root.setPadding(new javafx.geometry.Insets(10));

        Button showLowStockBtn = new Button("Show Low Stock Products");
        Button consumeScenario1Btn = new Button("Consume Scenario 1");
        Button consumeScenario2Btn = new Button("Consume Scenario 2");

        showLowStockBtn.setOnAction(e -> showLowStockProducts(root));
        consumeScenario1Btn.setOnAction(e -> runScenario(
                new Pair<>(1, 3), new Pair<>(2, 2)
        ));
        consumeScenario2Btn.setOnAction(e -> runScenario(
                new Pair<>(3, 5)
        ));

        root.getChildren().addAll(
                showLowStockBtn,
                consumeScenario1Btn,
                consumeScenario2Btn,
                resultLabel
        );

        return root;
    }

    private void showLowStockProducts(VBox root) {
        ProductTableViewBuilder builder = new ProductTableViewBuilder(productService);
        productTable = builder.buildProductTable();

        if (!root.getChildren().contains(productTable)) {
            root.getChildren().add(productTable);
        } else {
            productTable.setItems(FXCollections.observableArrayList(
                    productService.getProductsBelowThreshold()
            ));
        }
    }

    @SafeVarargs
    private void runScenario(Pair<Integer, Integer>... productPairs) {
        ConsumptionResponseDto response = consumptionService.consumeProduct(Arrays.asList(productPairs));
        resultLabel.setText(response.getMessage());

        if (productTable != null) {
            productTable.setItems(FXCollections.observableArrayList(
                    productService.getProductsBelowThreshold()
            ));
        }
    }
    }

