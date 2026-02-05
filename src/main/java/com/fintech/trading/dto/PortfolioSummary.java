package com.fintech.trading.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
@AllArgsConstructor
public class PortfolioSummary {
    private Long userId;
    private BigDecimal totalValuationUSD;
    private Map<String, BigDecimal> assetDistribution;
}