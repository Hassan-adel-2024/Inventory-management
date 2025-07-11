package com.inventoryapp.inventorymanagement.exception;

public class InvalidStockAmountException extends RuntimeException{
    public InvalidStockAmountException(String message) {
        super(message);
    }

    public InvalidStockAmountException(String message, Throwable cause) {
        super(message, cause);
    }

}
