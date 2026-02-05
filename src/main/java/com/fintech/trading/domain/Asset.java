package com.fintech.trading.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Entity
@Table(name = "assets")
@Data
public class Asset {
    @Id
    private String symbol; // e.g., "BTC", "USD"

    @Column(precision = 19, scale = 4)
    private BigDecimal price;

    // Versioning for Optimistic Locking
    @Version
    private Long version;
}