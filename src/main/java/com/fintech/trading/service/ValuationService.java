package com.fintech.trading.service;

import com.fintech.trading.dto.PortfolioSummary;

public interface ValuationService {
    PortfolioSummary getPortfolioValuation(Long userId);
}