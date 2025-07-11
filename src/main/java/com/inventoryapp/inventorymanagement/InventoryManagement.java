package com.inventoryapp.inventorymanagement;

import com.inventoryapp.inventorymanagement.db.DatabaseConfig;
import com.inventoryapp.inventorymanagement.ui.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class InventoryManagement extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        MainView mainView = new MainView();
        VBox root = (VBox) mainView.buildMainUI();

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        Image icon = new Image("D:\\Programming\\demo\\Inventory-management\\src\\icon.jpg");
        stage.getIcons().add(icon);
        stage.setTitle("Product Inventory Management");
        stage.show();
    }

    public static void main(String[] args) {

        launch(args);
    }
}