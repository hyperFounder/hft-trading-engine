package com.fintech.trading.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderRequest {
    private Long userId;
    private String symbol;     // Asset to Buy
    private String payWith;    // Asset to Sell (e.g., USD)
    private BigDecimal quantity;
}