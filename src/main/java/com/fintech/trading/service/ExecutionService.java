package com.fintech.trading.service;

import com.fintech.trading.dto.OrderRequest;
import java.util.concurrent.CompletableFuture;

public interface ExecutionService {
    CompletableFuture<String> executeMarketOrder(OrderRequest request);
}