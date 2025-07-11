package com.inventoryapp.inventorymanagement.ui;

import com.inventoryapp.inventorymanagement.service.impl.ServiceFactory;
import com.inventoryapp.inventorymanagement.service.IPurchaseOrderService;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.util.List;

public class RestockOrderComponent {
    private final VBox container = new VBox(10);

    public RestockOrderComponent() {
        Button restockBtn = new Button("Generate Restock Orders");
        restockBtn.setOnAction(e -> handleRestock());

        container.getChildren().add(restockBtn);
    }

    private void handleRestock() {
        IPurchaseOrderService purchaseOrderService = ServiceFactory.getPurchaseOrderService();
        List<String> notifications = ((com.inventoryapp.inventorymanagement.service.impl.PurchaseOrderService) purchaseOrderService)
                .createPurchaseOrdersForLowStockProducts();

        String message = String.join("\n", notifications);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Restock Orders");
        alert.setHeaderText("Restock Order Results");
        alert.setContentText(message);
        alert.showAndWait();
    }

    public Node getView() {
        return container;
    }
}
