package com.zagbor.wallet.dto;

import java.math.BigDecimal;
import java.util.List;

public record BudgetDto(
        BigDecimal totalExpense,
        BigDecimal totalIncome,
        List<SumDto> budgets,
        List<SumDto> incomes

) {

}
