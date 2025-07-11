package com.inventoryapp.inventorymanagement.model;

public class PurchaseOrderItem {
    private int orderItemID;
    private int orderID;
    private int productID;
    private double unitPrice;
    private int quantity;
    public PurchaseOrderItem() {

    }

    public PurchaseOrderItem(int orderItemID, int orderID, int productID, double unitPrice, int quantity) {
        this.orderItemID = orderItemID;
        this.orderID = orderID;
        this.productID = productID;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    public int getOrderItemID() {
        return orderItemID;
    }

    public void setOrderItemID(int orderItemID) {
        this.orderItemID = orderItemID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
