package com.zagbor.wallet.dto;

import java.math.BigDecimal;

public record SumDto(String categoryName,
                     BigDecimal sum,
                     BigDecimal limit) {
}
