package com.zagbor.wallet.model;


import java.math.BigDecimal;
import java.util.List;

public record Wallet(
        Long id,
        BigDecimal balance,
        List<Transaction> transactions,
        List<Category> budgetCategories
) {
    @Override
    public String toString() {
        return "Wallet{id=" + id +
                ", balance=" + balance +
                ", transactions=" + (transactions != null ? transactions.size() : 0) + " transactions" +
                ", budgetCategories=" + (budgetCategories != null ? budgetCategories.size() : 0) + " categories" +
                '}';
    }
}
