package com.inventoryapp.inventorymanagement.model;

public class Product {
    private int productId;
    private String name;
    private int currentStock;
    private int reorderThreshold;
    private double unitPrice;
    private int supplierId;

    public Product() {
    }

    public Product(int productId, String name, int currentStock, int reorderThreshold, double unitPrice, int supplierId) {
        this.productId = productId;
        this.name = name;
        this.currentStock = currentStock;
        this.reorderThreshold = reorderThreshold;
        this.unitPrice = unitPrice;
        this.supplierId = supplierId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public int getReorderThreshold() {
        return reorderThreshold;
    }

    public void setReorderThreshold(int reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

}
