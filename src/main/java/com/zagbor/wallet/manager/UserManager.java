package com.zagbor.wallet.manager;

import com.zagbor.wallet.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManager {

    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);

    private final Map<String, User> userMap; // Хранилище пользователей

    // Создание нового пользователя
    public User createUser(User user) {
        if (userMap.containsKey(user.username())) {
            logger.error("User creation failed: User with username '{}' already exists", user.username());
        }
        userMap.put(user.username(), user);
        logger.info("User with username '{}' successfully created", user.username());
        return user;
    }

    // Получение пользователя по имени
    public Optional<User> getUserByUsername(String username) {
        return Optional.ofNullable(userMap.get(username));
    }

    public boolean updateUser(String username, User updatedUser) {
        if (!userMap.containsKey(username)) {
            logger.warn("Failed to update user: User with username '{}' not found", username);
            return false;
        }
        userMap.put(username, updatedUser);
        logger.info("User with username '{}' successfully updated", username);
        return true;
    }


    // Метод для удаления пользователя по имени
    public boolean removeUser(String username) {
        if (userMap.containsKey(username)) {
            userMap.remove(username); // Удаляем пользователя из хранилища
            return true;
        }
        return false; // Если пользователя с таким именем не существует
    }

    // Обновление баланса пользователя
    public boolean updateBalance(String username, BigDecimal amount) {
        return getUserByUsername(username).map(user -> {
            Wallet updatedWallet = new Wallet(
                    user.wallet().id(),
                    user.wallet().balance().add(amount),
                    user.wallet().transactions(),
                    user.wallet().budgetCategories()
            );
            userMap.put(username, new User(user.id(), user.username(), updatedWallet));
            return true;
        }).orElse(false);
    }

    // Добавление транзакции
    public boolean addTransaction(String username, String name, TransactionType type, BigDecimal amount, String categoryName) {
        return getUserByUsername(username).map(user -> {
            Wallet wallet = user.wallet();
            Optional<Category> categoryOpt = wallet.budgetCategories().stream()
                    .filter(category -> category.name().equals(categoryName))
                    .findFirst();

            if (categoryOpt.isEmpty()
                    || type == TransactionType.EXPENSE && categoryOpt.get().currentLimit().compareTo(amount) < 0) {
                return false; // Лимит превышен или категории нет
            }

            Category category = categoryOpt.get();
            Transaction transaction = new Transaction(
                    (long) (wallet.transactions().size() + 1),
                    name,
                    type,
                    amount,
                    LocalDate.now(),
                    category,
                    wallet
            );

            List<Transaction> updatedTransactions = new ArrayList<>(wallet.transactions());
            updatedTransactions.add(transaction);

            Category updatedCategory = new Category(
                    category.id(),
                    category.name(),
                    category.budgetLimit(),
                    category.currentLimit().subtract(amount),
                    wallet
            );

            List<Category> updatedCategories = new ArrayList<>(wallet.budgetCategories());
            updatedCategories.set(updatedCategories.indexOf(category), updatedCategory);

            Wallet updatedWallet = new Wallet(
                    wallet.id(),
                    wallet.balance().add(type == TransactionType.INCOME ? amount : amount.negate()),
                    updatedTransactions,
                    updatedCategories
            );

            userMap.put(username, new User(user.id(), user.username(), updatedWallet));
            return true;
        }).orElse(false);
    }

    // Получение списка транзакций пользователя
    public List<Transaction> getUserTransactions(String username) {
        return getUserByUsername(username)
                .map(user -> user.wallet().transactions())
                .orElse(Collections.emptyList());
    }

    // Добавление категории бюджета
    public boolean addCategory(String username, String categoryName, BigDecimal budgetLimit) {
        return getUserByUsername(username).map(user -> {
            Wallet wallet = user.wallet();

            if (wallet.budgetCategories().stream().anyMatch(category -> category.name().equals(categoryName))) {
                return false; // Категория уже существует
            }

            Category category = new Category(
                    (long) (wallet.budgetCategories().size() + 1),
                    categoryName,
                    budgetLimit,
                    budgetLimit,
                    wallet
            );

            List<Category> updatedCategories = new ArrayList<>(wallet.budgetCategories());
            updatedCategories.add(category);

            Wallet updatedWallet = new Wallet(
                    wallet.id(),
                    wallet.balance(),
                    wallet.transactions(),
                    updatedCategories
            );

            userMap.put(username, new User(user.id(), user.username(), updatedWallet));
            return true;
        }).orElse(false);
    }

    public List<User> getAllUsers() {
        return userMap.values().stream().toList();
    }
}
