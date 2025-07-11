package com.inventoryapp.inventorymanagement;

import com.inventoryapp.inventorymanagement.db.DatabaseConfig;
import com.inventoryapp.inventorymanagement.ui.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class InventoryManagement extends Application {
    @Override
    public void start(Stage stage) throws IOException {
//        Group root = new Group();
//        Scene scene = new Scene(root,600, 400, Color.LIGHTBLUE);
//        Text title = new Text("Please hire me");
//        title.setX(50);
//        title.setY(50);
//        stage.setScene(scene);
//        root.getChildren().add(title);
//        stage.setTitle("Inventory Management");
//        Image icon = new Image("D:\\Programming\\demo\\Inventory-management\\src\\icon.jpg");
//        stage.getIcons().add(icon);
//
//        stage.show();
        MainView mainView = new MainView();
        VBox root = (VBox) mainView.buildMainUI();

        Scene scene = new Scene(root, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Product Inventory Management");
        stage.show();
    }

    public static void main(String[] args) {
        Connection conn = DatabaseConfig.getConnection();
        if (conn != null) {
            System.out.println("🎉 Connection test successful!");
        }
        DatabaseConfig.closeConnection();
        launch(args);
    }
}