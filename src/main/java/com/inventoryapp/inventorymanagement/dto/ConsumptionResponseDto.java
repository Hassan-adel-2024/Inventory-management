package com.inventoryapp.inventorymanagement.dto;

import com.inventoryapp.inventorymanagement.model.Product;

import java.util.List;

public class ConsumptionResponseDto {
    private  String message;
    private List<Product> consumedProducts;
    private boolean success;

    public ConsumptionResponseDto() {
    }


    public ConsumptionResponseDto(String message, List<Product> consumedProducts, boolean success) {
        this.message = message;
        this.consumedProducts = consumedProducts;
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Product> getConsumedProducts() {
        return consumedProducts;
    }

    public void setConsumedProducts(List<Product> consumedProducts) {
        this.consumedProducts = consumedProducts;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isSuccess() {
        return success;
    }
}
