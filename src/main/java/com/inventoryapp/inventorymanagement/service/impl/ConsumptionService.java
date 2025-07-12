package com.inventoryapp.inventorymanagement.service.impl;

import com.inventoryapp.inventorymanagement.beanfactory.DaoBeanFactory;
import com.inventoryapp.inventorymanagement.beanfactory.ServiceFactory;
import com.inventoryapp.inventorymanagement.dto.ConsumptionResponseDto;
import com.inventoryapp.inventorymanagement.model.Product;
import com.inventoryapp.inventorymanagement.service.IConsumptionService;
import com.inventoryapp.inventorymanagement.service.IProductService;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConsumptionService implements IConsumptionService {
    private static final Logger logger = Logger.getLogger(ConsumptionService.class.getName());
    private final IProductService productService;

//    public ConsumptionService() {
//        this.productService = ServiceFactory.getInstance().getService(ProductService.class);
//    }

    public ConsumptionService(IProductService productService) {
        this.productService = productService;
    }


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
                    throw new IllegalStateException("Insufficient stock for product: " + product.getName() +
                            " (Available: " + product.getCurrentStock() + ", Requested: " + quantityToConsume + ")");
                }
            }

            // Step 2: Perform stock updates
            List<String> consumedProducts = new ArrayList<>();
            List<String> lowStockWarnings = new ArrayList<>();

            for (Pair<Integer, Integer> pair : productQuantities) {
                int productId = pair.getKey();
                int quantityToConsume = pair.getValue();

                Product product = productService.getProductById(productId);
                int newStock = product.getCurrentStock() - quantityToConsume;
                product.setCurrentStock(newStock);
                productService.updateProduct(product);

                consumedProducts.add(String.format("%s (-%d)", product.getName(), quantityToConsume));

                // Check if stock is now below threshold
                if (newStock < product.getReorderThreshold()) {
                    lowStockWarnings.add(String.format("%s is now below reorder threshold (%d remaining)",
                            product.getName(), newStock));
                }
            }

            // Step 3: Build response message
            StringBuilder messageBuilder = new StringBuilder();
            messageBuilder.append("✓ Products consumed successfully:\n");
            for (String consumed : consumedProducts) {
                messageBuilder.append("• ").append(consumed).append("\n");
            }

            if (!lowStockWarnings.isEmpty()) {
                messageBuilder.append("\n⚠ Low stock warnings:\n");
                for (String warning : lowStockWarnings) {
                    messageBuilder.append("• ").append(warning).append("\n");
                }
            }

            response.setSuccess(true);
            response.setMessage(messageBuilder.toString().trim());

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to consume product stock", e);
            response.setSuccess(false);
            response.setMessage("Consumption failed: " + e.getMessage());
        }

        return response;
    }
}
