package com.zagbor.wallet.model;

import java.math.BigDecimal;

public record Category(
    Long id,
    String name,
    BigDecimal budgetLimit,
    BigDecimal currentLimit,
    Wallet wallet
) {}