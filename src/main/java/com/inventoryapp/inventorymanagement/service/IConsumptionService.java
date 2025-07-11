package com.inventoryapp.inventorymanagement.service;

import com.inventoryapp.inventorymanagement.dto.ConsumptionResponseDto;
import javafx.util.Pair;

import java.util.List;

public interface IConsumptionService {
    ConsumptionResponseDto consumeProduct(List<Pair<Integer, Integer>> productQuantities);
}
