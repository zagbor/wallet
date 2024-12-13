package com.zagbor.wallet.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class Wallet {
    private List<Category> budgetCategories;

    public Wallet() {
    }
}

