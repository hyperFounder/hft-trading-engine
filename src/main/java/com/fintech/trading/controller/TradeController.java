package com.fintech.trading.controller;

import com.fintech.trading.advice.Idempotent;
import com.fintech.trading.dto.OrderRequest;
import com.fintech.trading.dto.PortfolioSummary;
import com.fintech.trading.service.ExecutionService;
import com.fintech.trading.service.ValuationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/trading")
public class TradeController {

    private final ExecutionService executionService;
    private final ValuationService valuationService;

    public TradeController(ExecutionService executionService, ValuationService valuationService) {
        this.executionService = executionService;
        this.valuationService = valuationService;
    }

    @PostMapping("/execute")
    @Idempotent
    public CompletableFuture<ResponseEntity<String>> executeOrder(@RequestBody OrderRequest request) {
        return executionService.executeMarketOrder(request)
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/portfolio/{userId}")
    public ResponseEntity<PortfolioSummary> getPortfolio(@PathVariable Long userId) {
        return ResponseEntity.ok(valuationService.getPortfolioValuation(userId));
    }
}