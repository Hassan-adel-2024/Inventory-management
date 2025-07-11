package com.inventoryapp.inventorymanagement.ui;

public class ReportRow {
    private final int orderId;
    private final String supplierName;
    private final String productName;
    private final int quantity;
    private final double unitPrice;
    private final boolean delivered;

    public ReportRow(int orderId, String supplierName, String productName, int quantity, double unitPrice, boolean delivered) {
        this.orderId = orderId;
        this.supplierName = supplierName;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.delivered = delivered;
    }

    public int getOrderId() { return orderId; }
    public String getSupplierName() { return supplierName; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public boolean getDelivered() { return delivered; }
}
