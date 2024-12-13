package com.zagbor.wallet.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CategoryDto(
    Long id,
    @NotNull(message = "Name cannot be empty")
    String name,
    BigDecimal budgetLimit,
    BigDecimal spent,
    boolean excess,
    String type
) {}