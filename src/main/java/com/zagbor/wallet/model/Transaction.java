package com.zagbor.wallet.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Transaction {
    private String id;
    private String name;
    private BigDecimal amount;
    private LocalDateTime date;
    private String categoryName;
}
