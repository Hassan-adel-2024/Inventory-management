module com.inventoryapp.inventorymanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.inventoryapp.inventorymanagement to javafx.fxml;
    opens com.inventoryapp.inventorymanagement.model to javafx.base;
    opens com.inventoryapp.inventorymanagement.ui to javafx.base;
    exports com.inventoryapp.inventorymanagement;
}