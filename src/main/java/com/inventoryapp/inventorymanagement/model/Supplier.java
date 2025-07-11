package com.inventoryapp.inventorymanagement.model;

public class Supplier {
    private int supplierId;
    private String name;
    private String email;
    private String phone;
    private int deliveryTime; // days

    public Supplier() {
    }

    public Supplier(int supplierId, String name, String email, String phone, int deliveryTime) {
        this.supplierId = supplierId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.deliveryTime = deliveryTime;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(int deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
}
