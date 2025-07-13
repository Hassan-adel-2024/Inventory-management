package com.inventoryapp.inventorymanagement.ui;

import com.inventoryapp.inventorymanagement.beanfactory.ServiceFactory;
import com.inventoryapp.inventorymanagement.dao.impl.PurchaseOrderDaoImpl;
import com.inventoryapp.inventorymanagement.model.PurchaseOrder;
import com.inventoryapp.inventorymanagement.service.IPurchaseOrderService;
import com.inventoryapp.inventorymanagement.service.impl.PurchaseOrderService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Consumer;

public class MarkOrderDeliveredComponent {
    private final VBox container = new VBox(10);
    private final TableView<PurchaseOrder> tableView = new TableView<>();
    private final Label result = new Label();
    private Consumer<Void> refreshCallback;

    public MarkOrderDeliveredComponent() {
        this(null);
    }

    public MarkOrderDeliveredComponent(Consumer<Void> refreshCallback) {
        this.refreshCallback = refreshCallback;
        
        Label label = new Label("All Purchase Orders");
        setupTable();
        refreshTable();

        container.getChildren().addAll(label, tableView, result);
    }

    private void setupTable() {
        TableColumn<PurchaseOrder, Integer> idCol = new TableColumn<>("Order ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getOrderID()).asObject());

        TableColumn<PurchaseOrder, Integer> supplierCol = new TableColumn<>("Supplier ID");
        supplierCol.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getSupplierID()).asObject());

        TableColumn<PurchaseOrder, String> dateCol = new TableColumn<>("Created At");
        dateCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getCreatedAt().toString()));

        TableColumn<PurchaseOrder, String> deliveredCol = new TableColumn<>("Delivered");
        deliveredCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().isDelivered() ? "Yes" : "No"));

        TableColumn<PurchaseOrder, String> deletedCol = new TableColumn<>("Deleted");
        deletedCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().isDeleted() ? "Yes" : "No"));

        TableColumn<PurchaseOrder, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("Mark as Delivered");

            {
                btn.setOnAction(e -> {
                    PurchaseOrder order = getTableView().getItems().get(getIndex());
                    if (!order.isDelivered() && !order.isDeleted()) {
//                        PurchaseOrderService service = new PurchaseOrderService();
                        IPurchaseOrderService service = ServiceFactory.getInstance().getService(IPurchaseOrderService.class);
                        boolean success = false;
                        try {
                            success = service.markOrderAsDelivered(order.getOrderID());
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                        result.setText(success ? "Order marked as delivered." : "Failed to mark order.");
                        refreshTable();
                        
                        // Trigger refresh callback if provided
                        if (refreshCallback != null) {
                            refreshCallback.accept(null);
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    PurchaseOrder order = getTableView().getItems().get(getIndex());
                    btn.setDisable(order.isDelivered() || order.isDeleted());
                    setGraphic(btn);
                }
            }
        });

        tableView.getColumns().addAll(idCol, supplierCol, dateCol, deliveredCol, deletedCol, actionCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void refreshTable() {
        try {
            PurchaseOrderDaoImpl dao = new PurchaseOrderDaoImpl();
            List<PurchaseOrder> orders = dao.findAll();
            ObservableList<PurchaseOrder> data = FXCollections.observableArrayList(orders);
            tableView.setItems(data);
        } catch (Exception e) {
            result.setText("Failed to load orders");
        }
    }

    public Node getView() {
        return container;
    }

    public void hide() {
        container.setVisible(false);
        container.setManaged(false);
    }

    public void show() {
        container.setVisible(true);
        container.setManaged(true);
    }
}
