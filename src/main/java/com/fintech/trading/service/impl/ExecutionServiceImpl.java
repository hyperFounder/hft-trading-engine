package com.fintech.trading.service.impl;

import com.fintech.trading.domain.Asset;
import com.fintech.trading.domain.Portfolio;
import com.fintech.trading.dto.OrderRequest;
import com.fintech.trading.exception.InsufficientFundsException;
import com.fintech.trading.repository.AssetRepository;
import com.fintech.trading.repository.PortfolioRepository;
import com.fintech.trading.service.ExecutionService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Service
public class ExecutionServiceImpl implements ExecutionService {

    private final PortfolioRepository portfolioRepository;
    private final AssetRepository assetRepository;  // <--- NEW: Need this to get prices
    private final MessageSource messageSource;

    public ExecutionServiceImpl(PortfolioRepository portfolioRepository,
                                AssetRepository assetRepository,
                                MessageSource messageSource) {
        this.portfolioRepository = portfolioRepository;
        this.assetRepository = assetRepository;
        this.messageSource = messageSource;
    }

    @Override
    @Async("tradeExecutor")
    @Transactional
    public CompletableFuture<String> executeMarketOrder(OrderRequest request) {

        // 1. Fetch Asset Price (e.g., Get BTC Price)
        Asset assetToBuy = assetRepository.findById(request.getSymbol())
                .orElseThrow(() -> new RuntimeException("Asset not found: " + request.getSymbol()));

        BigDecimal pricePerUnit = assetToBuy.getPrice();
        BigDecimal requiredAmount = pricePerUnit.multiply(request.getQuantity()); // Total Cost

        // 2. Lock Wallet (USD)
        Portfolio wallet = portfolioRepository.findByUserIdAndSymbolForUpdate(request.getUserId(), request.getPayWith())
                .orElseThrow(() -> new InsufficientFundsException("Wallet not found: " + request.getPayWith()));

        // 3. Validate Funds (Compare Balance vs Total Cost)
        if (wallet.getQuantity().compareTo(requiredAmount) < 0) {
            throw new InsufficientFundsException(wallet.getQuantity().toString());
        }

        // 4. Deduct Cost (USD)
        wallet.setQuantity(wallet.getQuantity().subtract(requiredAmount));
        portfolioRepository.save(wallet);

        // 5. Credit Asset (BTC)
        Portfolio targetAsset = portfolioRepository.findByUserIdAndSymbolForUpdate(request.getUserId(), request.getSymbol())
                .orElse(new Portfolio());

        if (targetAsset.getId() == null) {
            targetAsset.setUserId(request.getUserId());
            targetAsset.setAssetSymbol(request.getSymbol());
            targetAsset.setQuantity(BigDecimal.ZERO);
        }

        targetAsset.setQuantity(targetAsset.getQuantity().add(request.getQuantity()));
        portfolioRepository.save(targetAsset);

        // 6. Return Success Message
        String successMsg = messageSource.getMessage(
                "trade.order.filled",
                new Object[]{request.getSymbol()},
                LocaleContextHolder.getLocale()
        );

        return CompletableFuture.completedFuture(successMsg);
    }
}