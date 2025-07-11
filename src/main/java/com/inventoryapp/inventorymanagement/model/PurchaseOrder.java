package com.inventoryapp.inventorymanagement.model;

import java.util.Date;

public class PurchaseOrder {
    private int orderID;
    private int supplierID;
    private Date createdAt;
    private boolean isDelivered;
    private boolean isDeleted;

    public PurchaseOrder() {}

    public PurchaseOrder(int orderID, int supplierID, Date createdAt, boolean isDelivered) {
        this.orderID = orderID;
        this.supplierID = supplierID;
        this.createdAt = createdAt;
        this.isDelivered = isDelivered;
        this.isDeleted = false;
    }

    public PurchaseOrder(int orderID, int supplierID, Date createdAt, boolean isDelivered, boolean isDeleted) {
        this.orderID = orderID;
        this.supplierID = supplierID;
        this.createdAt = createdAt;
        this.isDelivered = isDelivered;
        this.isDeleted = isDeleted;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
