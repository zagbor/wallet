package com.zagbor.wallet.repository;

import com.zagbor.wallet.model.Transaction;
import com.zagbor.wallet.model.User;
import com.zagbor.wallet.service.UserService;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class TransactionRepository {

    private final UserService service;

    public Transaction getTransactionById(String username, String transactionId) {

        User user = service.getUserByUsername(username);
        if (user == null || user.getWallet() == null || user.getWallet().getBudgetCategories() == null) {
            return null; // Или выбросьте исключение
        }

        return user.getWallet().getBudgetCategories().stream()
                .flatMap(category -> category.getTransactions() == null
                        ? Stream.empty()
                        : category.getTransactions().stream())
                .filter(transaction -> transaction.getId().equals(transactionId)).findFirst().orElseThrow();
    }

    public void updateTransactions(String username, String categoryName, List<Transaction> transactionList) {
        User user = service.getUserByUsername(username);
        user.getWallet().getBudgetCategories().stream()
                .filter(category -> category.getName().equals(categoryName))
                .findFirst()
                .ifPresent(category -> category.setTransactions(transactionList));
    }

    public void deleteTransaction(String username, String transactionId) {
        // Получаем пользователя по имени
        User user = service.getUserByUsername(username);

        // Проверяем, что пользователь существует
        if (user == null) {
            return;
        }

        // Проходим по всем категориям и удаляем транзакцию с заданным ID
        user.getWallet().getBudgetCategories().forEach(category ->
                category.getTransactions().removeIf(transaction -> transaction.getId().equals(transactionId))
        );
    }

}
