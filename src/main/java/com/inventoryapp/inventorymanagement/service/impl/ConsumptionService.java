package com.inventoryapp.inventorymanagement.service.impl;

import com.inventoryapp.inventorymanagement.dto.ConsumptionResponseDto;
import com.inventoryapp.inventorymanagement.model.Product;
import com.inventoryapp.inventorymanagement.service.IConsumptionService;
import javafx.util.Pair;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsumptionService implements IConsumptionService {
    private static final Logger logger = Logger.getLogger(ConsumptionService.class.getName());
    private final ProductService productService;

    public ConsumptionService(ProductService productService) {
        this.productService = productService;
    }

    /*
     @Param productQuantities A list of pairs where each pair contains a product ID and the quantity to consume.
     */
    @Override
    public ConsumptionResponseDto consumeProduct(List<Pair<Integer, Integer>> productQuantities) {
        ConsumptionResponseDto response = new ConsumptionResponseDto();

        try {
            // Step 1: Validate all stocks
            for (Pair<Integer, Integer> pair : productQuantities) {
                int productId = pair.getKey();
                int quantityToConsume = pair.getValue();

                Product product = productService.getProductById(productId);
                if (product == null) {
                    throw new IllegalArgumentException("Product not found: ID = " + productId);
                }

                if (product.getCurrentStock() < quantityToConsume) {
                    throw new IllegalStateException("Insufficient stock for product ID: " + productId);
                }
            }

            // Step 2: Perform stock updates
            for (Pair<Integer, Integer> pair : productQuantities) {
                int productId = pair.getKey();
                int quantityToConsume = pair.getValue();

                Product product = productService.getProductById(productId);
                int newStock = product.getCurrentStock() - quantityToConsume;
                productService.updateProductStock(productId, newStock);
            }

            response.setSuccess(true);
            response.setMessage("All product stocks consumed successfully.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to consume product stock", e);
            response.setSuccess(false);
            response.setMessage("Consumption failed: " + e.getMessage());
        }

        return response;
    }
}

