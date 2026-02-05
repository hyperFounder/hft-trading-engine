package com.fintech.trading.service.impl;

import com.fintech.trading.domain.Asset;
import com.fintech.trading.domain.Portfolio;
import com.fintech.trading.dto.PortfolioSummary;
import com.fintech.trading.repository.AssetRepository;
import com.fintech.trading.repository.PortfolioRepository;
import com.fintech.trading.service.ValuationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ValuationServiceImpl implements ValuationService {

    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;

    public ValuationServiceImpl(PortfolioRepository portfolioRepository, AssetRepository assetRepository) {
        this.portfolioRepository = portfolioRepository;
        this.assetRepository = assetRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PortfolioSummary getPortfolioValuation(Long userId) {
        List<Portfolio> holdings = portfolioRepository.findByUserId(userId);
        BigDecimal totalValue = BigDecimal.ZERO;
        Map<String, BigDecimal> distribution = new HashMap<>();

        for (Portfolio p : holdings) {
            Asset asset = assetRepository.findById(p.getAssetSymbol()).orElse(null);
            BigDecimal price = (asset != null) ? asset.getPrice() : BigDecimal.ZERO;

            BigDecimal value = p.getQuantity().multiply(price);
            totalValue = totalValue.add(value);
            distribution.put(p.getAssetSymbol(), p.getQuantity());
        }

        return new PortfolioSummary(userId, totalValue, distribution);
    }
}