package com.inventoryapp.inventorymanagement.ui;

import com.inventoryapp.inventorymanagement.service.impl.ConsumptionService;
import com.inventoryapp.inventorymanagement.service.impl.ProductService;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class MainView {
    private final ProductService productService = new ProductService();
    private final ConsumptionService consumptionService = new ConsumptionService(productService);

    private final Label resultLabel = new Label();
    private LowStockComponent lowStockComponent;
    private ProductConsumptionComponent consumptionComponent;
    private PurchaseOrderReportComponent purchaseOrderReportComponent;
    private ProductsBelowThresholdNotReorderedComponent productsBelowThresholdNotReorderedComponent;
    private MarkOrderDeliveredComponent markOrderDeliveredComponent;
    private MarkOrderDeletedComponent markOrderDeletedComponent;

    // Track which components are currently shown to prevent duplicates
    private boolean lowStockShown = false;
    private boolean consumptionShown = false;
    private boolean purchaseOrderReportShown = false;
    private boolean productsBelowThresholdNotReorderedShown = false;

    private VBox root;

    public Node buildMainUI() {
        root = new VBox(10);
        root.setPadding(new Insets(10));

        // Reordered buttons for logical flow
        Button consumeBtn = new Button("Consume Products");
        Button showLowStockBtn = new Button("Show Low Stock Products");
        Button showProductsBelowThresholdNotReorderedBtn = new Button("Products Below Threshold Not Reordered");
        Button showPurchaseOrderReportBtn = new Button("Purchase Order Report");
        Button markOrderDeliveredBtn = new Button("Mark Order as Delivered");
        Button markOrderDeletedBtn = new Button("Mark Order as Deleted");
        Button hideAllBtn = new Button("Hide All Tables");

        consumeBtn.setOnAction(e -> toggleConsumptionUI());
        showLowStockBtn.setOnAction(e -> toggleLowStockProducts());
        showProductsBelowThresholdNotReorderedBtn.setOnAction(e -> toggleProductsBelowThresholdNotReordered());
        showPurchaseOrderReportBtn.setOnAction(e -> togglePurchaseOrderReport());
        markOrderDeliveredBtn.setOnAction(e -> toggleMarkOrderDelivered());
        markOrderDeletedBtn.setOnAction(e -> toggleMarkOrderDeleted());
        hideAllBtn.setOnAction(e -> hideAllComponents());

        root.getChildren().addAll(
            consumeBtn,
            showLowStockBtn,
            showProductsBelowThresholdNotReorderedBtn,
            showPurchaseOrderReportBtn,
            markOrderDeliveredBtn,
            markOrderDeletedBtn,
            hideAllBtn,
            resultLabel
        );

        // Add RestockOrderComponent so the button is always visible
        RestockOrderComponent restockOrderComponent = new RestockOrderComponent(v -> {
            // Refresh the products below threshold component if it's shown
            if (productsBelowThresholdNotReorderedComponent != null && productsBelowThresholdNotReorderedShown) {
                productsBelowThresholdNotReorderedComponent.refreshData();
            }
        });
        root.getChildren().add(restockOrderComponent.getView());

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
        // Refresh data when showing the component
        productsBelowThresholdNotReorderedComponent.refreshData();
    }

    private void hideProductsBelowThresholdNotReorderedComponent() {
        if (productsBelowThresholdNotReorderedComponent != null && productsBelowThresholdNotReorderedShown) {
            root.getChildren().remove(productsBelowThresholdNotReorderedComponent.getView());
            productsBelowThresholdNotReorderedShown = false;
        }
    }

    private void toggleMarkOrderDelivered() {
        if (markOrderDeliveredComponent == null) {
            markOrderDeliveredComponent = new MarkOrderDeliveredComponent(v -> {
                // Refresh the products below threshold component if it's shown
                if (productsBelowThresholdNotReorderedComponent != null && productsBelowThresholdNotReorderedShown) {
                    productsBelowThresholdNotReorderedComponent.refreshData();
                }
            });
            root.getChildren().add(markOrderDeliveredComponent.getView());
        }
        Node view = markOrderDeliveredComponent.getView();
        if (view.isVisible()) {
            markOrderDeliveredComponent.hide();
        } else {
            markOrderDeliveredComponent.show();
        }
    }

    private void toggleMarkOrderDeleted() {
        if (markOrderDeletedComponent == null) {
            markOrderDeletedComponent = new MarkOrderDeletedComponent(v -> {
                // Refresh the products below threshold component if it's shown
                if (productsBelowThresholdNotReorderedComponent != null && productsBelowThresholdNotReorderedShown) {
                    productsBelowThresholdNotReorderedComponent.refreshData();
                }
            });
            root.getChildren().add(markOrderDeletedComponent.getView());
        }
        Node view = markOrderDeletedComponent.getView();
        if (view.isVisible()) {
            markOrderDeletedComponent.hide();
        } else {
            markOrderDeletedComponent.show();
        }
    }

    private void hideAllComponents() {
        hideLowStockComponent();
        hideConsumptionComponent();
        hidePurchaseOrderReportComponent();
        hideProductsBelowThresholdNotReorderedComponent();
        if (markOrderDeliveredComponent != null) {
            markOrderDeliveredComponent.hide();
        }
        if (markOrderDeletedComponent != null) {
            markOrderDeletedComponent.hide();
        }
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
