package com.zagbor.wallet.service;

import com.zagbor.wallet.dto.BudgetDto;
import com.zagbor.wallet.dto.CategoryDto;
import com.zagbor.wallet.dto.SumDto;
import com.zagbor.wallet.dto.TransactionDto;
import com.zagbor.wallet.mapper.CategoryMapper;
import com.zagbor.wallet.mapper.TransactionMapper;
import com.zagbor.wallet.model.TransactionType;
import com.zagbor.wallet.model.Wallet;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BudgetService {

    private final UserService userService;
    private final CategoryMapper categoryMapper;
    private final TransactionMapper transactionMapper;
    private final CategoryService categoryService;

    public BudgetDto createBudget(String username) {
        Wallet wallet = userService.getUserByUsername(username).getWallet();

        List<TransactionDto> transactionIncomeDtos = getTransactionDtosByType(wallet, TransactionType.INCOME);

        List<TransactionDto> transactionExpenseDtos = getTransactionDtosByType(wallet, TransactionType.EXPENSE);

        List<CategoryDto> categoryDtos = categoryService.getBudgetCategories(username).stream()
                .map(categoryMapper::toDto)
                .toList();

        List<SumDto> incomesSum = calculateSumsByType(transactionIncomeDtos, null);

        List<SumDto> expensesSum = calculateSumsByType(transactionExpenseDtos, categoryDtos);

        BigDecimal totalIncome = calculateTotalByType(transactionIncomeDtos);
        BigDecimal totalExpense = calculateTotalByType(transactionExpenseDtos);

        return new BudgetDto(totalExpense, totalIncome, expensesSum, incomesSum);
    }

    private List<SumDto> calculateSumsByType(List<TransactionDto> transactions, List<CategoryDto> categoryDtos) {
        return transactions.stream()
                .collect(Collectors.groupingBy(TransactionDto::categoryName, Collectors.mapping(TransactionDto::amount,
                        Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))))
                .entrySet()
                .stream()
                .map(entry -> new SumDto(
                        entry.getKey(),
                        entry.getValue(),
                        categoryDtos == null ? null : getCategoryLimit(entry.getKey(), categoryDtos)
                ))
                .toList();
    }

    private BigDecimal calculateTotalByType(List<TransactionDto> transactions) {
        return transactions.stream()
                .map(TransactionDto::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal getCategoryLimit(String categoryName, List<CategoryDto> categoryDtos) {
        return categoryDtos.stream()
                .filter(category -> category.name().equals(categoryName))
                .findFirst()
                .map(CategoryDto::budgetLimit)
                .orElse(null);
    }

    public List<TransactionDto> getTransactionDtosByType(Wallet wallet, TransactionType type) {
        return wallet.getBudgetCategories().stream()
                .filter(category -> category.getType().equals(type))
                .flatMap(category -> category.getTransactions().stream())
                .map(transactionMapper::toDto)
                .collect(Collectors.toList());
    }
}
