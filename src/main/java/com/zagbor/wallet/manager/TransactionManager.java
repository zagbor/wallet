package com.zagbor.wallet.manager;

import com.zagbor.wallet.model.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionManager {

    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);

    private final UserManager userService;

    // Добавление транзакции
    public boolean addTransaction(String username, String name, TransactionType type, BigDecimal amount, String categoryName) {
        logger.info("Attempting to add transaction: username={}, type={}, amount={}, categoryName={}",
                username, type, amount, categoryName);

        Optional<User> userOpt = userService.getUserByUsername(username);
        if (userOpt.isEmpty()) {
            logger.warn("User '{}' not found", username);
            return false; // Пользователь не найден
        }

        User user = userOpt.get();
        Wallet wallet = user.wallet();

        // Проверка существования категории
        Optional<Category> categoryOpt = wallet.budgetCategories().stream()
                .filter(category -> category.name().equals(categoryName))
                .findFirst();

        if (categoryOpt.isEmpty()) {
            logger.warn("Category '{}' not found for user '{}'", categoryName, username);
            return false; // Категория не найдена
        }

        Category category = categoryOpt.get();

        // Проверка лимита категории для расходов
        if (type == TransactionType.EXPENSE && category.currentLimit().compareTo(amount) < 0) {
            logger.warn("Insufficient funds in category '{}' for expense transaction: amount={}", categoryName, amount);
            return false; // Недостаточно средств в категории
        }

        // Создаем транзакцию
        Transaction transaction = new Transaction(
                (long) (wallet.transactions().size() + 1),
                name,
                type,
                amount,
                LocalDate.now(),
                category,
                wallet
        );

        // Обновляем список транзакций
        List<Transaction> updatedTransactions = new ArrayList<>(wallet.transactions());
        updatedTransactions.add(transaction);

        // Обновляем категорию
        BigDecimal updatedLimit = type == TransactionType.EXPENSE
                ? category.currentLimit().subtract(amount)
                : category.currentLimit();
        Category updatedCategory = new Category(
                category.id(),
                category.name(),
                category.budgetLimit(),
                updatedLimit,
                wallet
        );

        List<Category> updatedCategories = new ArrayList<>(wallet.budgetCategories());
        updatedCategories.set(updatedCategories.indexOf(category), updatedCategory);

        // Обновляем баланс кошелька
        BigDecimal updatedBalance = type == TransactionType.INCOME
                ? wallet.balance().add(amount)
                : wallet.balance().subtract(amount);

        Wallet updatedWallet = new Wallet(
                wallet.id(),
                updatedBalance,
                updatedTransactions,
                updatedCategories
        );

        // Сохраняем обновленного пользователя
        User updatedUser = new User(user.id(), user.username(), updatedWallet);
        userService.updateUser(username, updatedUser);

        logger.info("Transaction successfully added for user '{}': type={}, amount={}, category={}", username, type,
                amount, categoryName);
        return true;
    }

    // Получение списка транзакций пользователя
    public List<Transaction> getTransactions(String username) {
        logger.info("Fetching transactions for user '{}'", username);
        return userService.getUserByUsername(username)
                .map(user -> user.wallet().transactions())
                .orElse(new ArrayList<>());
    }

    // Удаление транзакции
    public boolean removeTransaction(String username, Long transactionId) {
        logger.info("Attempting to remove transaction: username={}, transactionId={}", username, transactionId);

        Optional<User> userOpt = userService.getUserByUsername(username);
        if (userOpt.isEmpty()) {
            logger.warn("User '{}' not found", username);
            return false; // Пользователь не найден
        }

        User user = userOpt.get();
        Wallet wallet = user.wallet();

        Optional<Transaction> transactionOpt = wallet.transactions().stream()
                .filter(transaction -> transaction.id().equals(transactionId))
                .findFirst();

        if (transactionOpt.isEmpty()) {
            logger.warn("Transaction '{}' not found for user '{}'", transactionId, username);
            return false; // Транзакция не найдена
        }

        Transaction transaction = transactionOpt.get();

        // Удаляем транзакцию из списка
        List<Transaction> updatedTransactions = new ArrayList<>(wallet.transactions());
        updatedTransactions.remove(transaction);

        // Возвращаем сумму в категорию, если это расход
        List<Category> updatedCategories = new ArrayList<>(wallet.budgetCategories());
        if (transaction.type() == TransactionType.EXPENSE) {
            Category category = transaction.category();
            Category updatedCategory = new Category(
                    category.id(),
                    category.name(),
                    category.budgetLimit(),
                    category.currentLimit().add(transaction.amount()),
                    wallet
            );
            updatedCategories.set(updatedCategories.indexOf(category), updatedCategory);
        }

        // Обновляем баланс кошелька
        BigDecimal updatedBalance = transaction.type() == TransactionType.INCOME
                ? wallet.balance().subtract(transaction.amount())
                : wallet.balance().add(transaction.amount());

        Wallet updatedWallet = new Wallet(
                wallet.id(),
                updatedBalance,
                updatedTransactions,
                updatedCategories
        );

        // Сохраняем обновленного пользователя
        User updatedUser = new User(user.id(), user.username(), updatedWallet);
        userService.updateUser(username, updatedUser);

        logger.info("Transaction successfully removed for user '{}': transactionId={}", username, transactionId);
        return true;
    }
}
