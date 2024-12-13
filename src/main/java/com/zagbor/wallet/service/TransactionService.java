package com.zagbor.wallet.service;

import com.zagbor.wallet.model.*;
import com.zagbor.wallet.repository.TransactionRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    private final UserService userService;
    private final CategoryService categoryService;
    private final TransactionRepository transactionRepository;

    // Добавление транзакции
    public boolean addTransaction(String username, String name, BigDecimal amount,
                                  String categoryName) {
        logger.info("Attempting to add transaction: username={},  amount={}, categoryName={}",
                username, amount, categoryName);

        Category category = categoryService.getCategoryByName(username, categoryName);

        Transaction transaction = new Transaction(
                UUID.randomUUID().toString(),
                name,
                amount,
                LocalDateTime.now(),
                category.getName()
        );

        List<Transaction> updatedTransactions = new ArrayList<>(category.getTransactions());
        updatedTransactions.add(transaction);
        transactionRepository.updateTransactions(username, categoryName, updatedTransactions);

        categoryService.updateCategorySpent(username, categoryName, amount, false);

        logger.info("Transaction successfully added for user '{}': amount={}, category={}", username,
                amount, categoryName);
        return true;
    }

    public List<Transaction> getTransactions(String username) {
        logger.info("Fetching transactions for user '{}'", username);
        return userService.getUserByUsername(username).getWallet().getBudgetCategories().stream()
                .flatMap(category -> category.getTransactions().stream()).toList();
    }

    // Удаление транзакции
    public boolean removeTransaction(String username, String transactionId) {
        logger.info("Attempting to remove transaction: username={}, transactionId={}", username, transactionId);

        Transaction transaction = transactionRepository.getTransactionById(username, transactionId);

        transactionRepository.deleteTransaction(username, transactionId);

        categoryService.updateCategorySpent(username, transaction.getCategoryName(),
                transaction.getAmount(), true);

        logger.info("Transaction successfully removed for user '{}': transactionId={}", username, transactionId);
        return true;
    }


}
