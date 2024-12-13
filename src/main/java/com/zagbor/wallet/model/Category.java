package com.zagbor.wallet.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "categoryId")
@Data
@AllArgsConstructor
public class Category {

    private String categoryId;
    private String name;
    private BigDecimal budgetLimit;
    private BigDecimal spent;
    private boolean excess;
    private List<Transaction> transactions;
    private TransactionType type;
}