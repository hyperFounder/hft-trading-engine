package com.fintech.trading.repository;

import com.fintech.trading.domain.Portfolio;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {

    // PESSIMISTIC_WRITE locks the row for the duration of the transaction
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Portfolio p WHERE p.userId = :userId AND p.assetSymbol = :symbol")
    Optional<Portfolio> findByUserIdAndSymbolForUpdate(@Param("userId") Long userId, @Param("symbol") String symbol);

    List<Portfolio> findByUserId(Long userId);
}