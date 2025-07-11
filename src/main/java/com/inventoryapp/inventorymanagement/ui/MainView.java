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
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Callback;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainView {
    private final ProductService productService = new ProductService();
    private final ConsumptionService consumptionService = new ConsumptionService(productService);

    private final Label resultLabel = new Label();
    private LowStockComponent lowStockComponent;
    private ProductConsumptionComponent consumptionComponent;
    private PurchaseOrderReportComponent purchaseOrderReportComponent;
    private ProductsBelowThresholdNotReorderedComponent productsBelowThresholdNotReorderedComponent;

    // Track which components are currently shown to prevent duplicates
    private boolean lowStockShown = false;
    private boolean consumptionShown = false;
    private boolean purchaseOrderReportShown = false;
    private boolean productsBelowThresholdNotReorderedShown = false;

    private VBox root;

    public Node buildMainUI() {
        root = new VBox(10);
        root.setPadding(new Insets(10));

        Button showLowStockBtn = new Button("Show Low Stock Products");
        Button consumeBtn = new Button("Consume Products");
        Button showPurchaseOrderReportBtn = new Button("Purchase Order Report");
        Button showProductsBelowThresholdNotReorderedBtn = new Button("Products Below Threshold Not Reordered");
        Button hideAllBtn = new Button("Hide All Tables");

        showLowStockBtn.setOnAction(e -> toggleLowStockProducts());
        consumeBtn.setOnAction(e -> toggleConsumptionUI());
        showPurchaseOrderReportBtn.setOnAction(e -> togglePurchaseOrderReport());
        showProductsBelowThresholdNotReorderedBtn.setOnAction(e -> toggleProductsBelowThresholdNotReordered());
        hideAllBtn.setOnAction(e -> hideAllComponents());

        root.getChildren().addAll(
            showLowStockBtn,
            consumeBtn,
            showPurchaseOrderReportBtn,
            showProductsBelowThresholdNotReorderedBtn,
            hideAllBtn,
            resultLabel
        );

        return root;
    }

    private void toggleLowStockProducts() {
        if (lowStockShown) {
            hideLowStockComponent();
        } else {
            showLowStockProducts();
        }
    }

    private void showLowStockProducts() {
        if (lowStockComponent == null) {
            lowStockComponent = new LowStockComponent(productService);
        }

        if (!lowStockShown) {
            root.getChildren().add(lowStockComponent.getView());
            lowStockShown = true;
        }

        lowStockComponent.refreshData();
    }

    private void hideLowStockComponent() {
        if (lowStockComponent != null && lowStockShown) {
            root.getChildren().remove(lowStockComponent.getView());
            lowStockShown = false;
        }
    }

    private void toggleConsumptionUI() {
        if (consumptionShown) {
            hideConsumptionComponent();
        } else {
            showConsumptionUI();
        }
    }

    private void showConsumptionUI() {
        if (consumptionComponent == null) {
            consumptionComponent = new ProductConsumptionComponent(
                    productService,
                    consumptionService,
                    this::updateResultLabel
            );
        }

        if (!consumptionShown) {
            root.getChildren().add(consumptionComponent.getView());
            consumptionShown = true;
        }

        consumptionComponent.refreshData();
    }

    private void hideConsumptionComponent() {
        if (consumptionComponent != null && consumptionShown) {
            root.getChildren().remove(consumptionComponent.getView());
            consumptionShown = false;
        }
    }

    private void togglePurchaseOrderReport() {
        if (purchaseOrderReportShown) {
            hidePurchaseOrderReportComponent();
        } else {
            showPurchaseOrderReport();
        }
    }

    private void showPurchaseOrderReport() {
        if (purchaseOrderReportComponent == null) {
            purchaseOrderReportComponent = new PurchaseOrderReportComponent();
        }
        if (!purchaseOrderReportShown) {
            root.getChildren().add(purchaseOrderReportComponent.getView());
            purchaseOrderReportShown = true;
        }
    }

    private void hidePurchaseOrderReportComponent() {
        if (purchaseOrderReportComponent != null && purchaseOrderReportShown) {
            root.getChildren().remove(purchaseOrderReportComponent.getView());
            purchaseOrderReportShown = false;
        }
    }

    private void toggleProductsBelowThresholdNotReordered() {
        if (productsBelowThresholdNotReorderedShown) {
            hideProductsBelowThresholdNotReorderedComponent();
        } else {
            showProductsBelowThresholdNotReordered();
        }
    }

    private void showProductsBelowThresholdNotReordered() {
        if (productsBelowThresholdNotReorderedComponent == null) {
            productsBelowThresholdNotReorderedComponent = new ProductsBelowThresholdNotReorderedComponent();
        }
        if (!productsBelowThresholdNotReorderedShown) {
            root.getChildren().add(productsBelowThresholdNotReorderedComponent.getView());
            productsBelowThresholdNotReorderedShown = true;
        }
    }

    private void hideProductsBelowThresholdNotReorderedComponent() {
        if (productsBelowThresholdNotReorderedComponent != null && productsBelowThresholdNotReorderedShown) {
            root.getChildren().remove(productsBelowThresholdNotReorderedComponent.getView());
            productsBelowThresholdNotReorderedShown = false;
        }
    }

    private void hideAllComponents() {
        hideLowStockComponent();
        hideConsumptionComponent();
        hidePurchaseOrderReportComponent();
        hideProductsBelowThresholdNotReorderedComponent();
        clearResultLabel();
    }

    public void updateResultLabel(String message, boolean isSuccess) {
        resultLabel.setText(message);
        resultLabel.setTextFill(isSuccess ? Color.DARKGREEN : Color.FIREBRICK);
    }

    public void clearResultLabel() {
        resultLabel.setText("");
    }
}
