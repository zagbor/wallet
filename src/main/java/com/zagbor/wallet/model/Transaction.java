package com.zagbor.wallet.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Transaction(
        Long id,
        String name,
        TransactionType type,
        BigDecimal amount,
        LocalDate date,
        Category category,
        Wallet wallet
) {
}