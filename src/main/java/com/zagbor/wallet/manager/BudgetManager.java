package com.zagbor.wallet.manager;

import com.zagbor.wallet.model.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class BudgetManager {

    private static final Logger logger = LoggerFactory.getLogger(BudgetManager.class);

    private final UserManager userManager;

    // Получение списка категорий бюджета пользователя
    public List<Category> getBudgetCategories(String username) {
        logger.info("Fetching budget categories for user '{}'", username);
        Optional<User> userOpt = userManager.getUserByUsername(username);
        return userOpt.map(user -> user.wallet().budgetCategories()).orElse(new ArrayList<>());
    }

    // Добавление категории в бюджет пользователя
    public boolean addCategoryToBudget(String username, String categoryName, BigDecimal budgetLimit) {
        logger.info("Adding category '{}' to budget for user '{}'", categoryName, username);
        Optional<User> userOpt = userManager.getUserByUsername(username);
        if (userOpt.isEmpty()) {
            logger.warn("User '{}' not found, unable to add category", username);
            return false;
        }

        User user = userOpt.get();
        Wallet wallet = user.wallet();

        // Проверка на существование категории
        Optional<Category> existingCategoryOpt = wallet.budgetCategories().stream()
                .filter(category -> category.name().equals(categoryName))
                .findFirst();

        if (existingCategoryOpt.isPresent()) {
            logger.warn("Category '{}' already exists in the budget for user '{}'", categoryName, username);
            return false; // Категория уже существует
        }

        // Создаем новую категорию
        Category newCategory = new Category(
                (long) (wallet.budgetCategories().size() + 1), // ID категории
                categoryName,
                budgetLimit,
                budgetLimit, // Начальный лимит равен бюджету
                wallet
        );

        List<Category> updatedCategories = new ArrayList<>(wallet.budgetCategories());
        updatedCategories.add(newCategory);

        Wallet updatedWallet = new Wallet(
                wallet.id(),
                wallet.balance(),
                wallet.transactions(),
                updatedCategories
        );

        // Обновляем пользователя с новым кошельком
        User updatedUser = new User(user.id(), user.username(), updatedWallet);
        userManager.updateUser(username, updatedUser);

        logger.info("Category '{}' successfully added to budget for user '{}'", categoryName, username);
        return true;
    }

    // Обновление лимита категории бюджета
    public boolean updateCategoryLimit(String username, String categoryName, BigDecimal newLimit) {
        logger.info("Updating category '{}' limit to '{}' for user '{}'", categoryName, newLimit, username);
        Optional<User> userOpt = userManager.getUserByUsername(username);
        if (userOpt.isEmpty()) {
            logger.warn("User '{}' not found, unable to update category limit", username);
            return false;
        }

        User user = userOpt.get();
        Wallet wallet = user.wallet();

        // Поиск категории по имени
        Optional<Category> categoryOpt = wallet.budgetCategories().stream()
                .filter(category -> category.name().equals(categoryName))
                .findFirst();

        if (categoryOpt.isEmpty()) {
            logger.warn("Category '{}' not found in the budget for user '{}'", categoryName, username);
            return false; // Категория не найдена
        }

        Category category = categoryOpt.get();
        Category updatedCategory = new Category(
                category.id(),
                category.name(),
                category.budgetLimit(),
                newLimit, // Обновляем лимит
                wallet
        );

        List<Category> updatedCategories = new ArrayList<>(wallet.budgetCategories());
        updatedCategories.set(updatedCategories.indexOf(category), updatedCategory);

        Wallet updatedWallet = new Wallet(
                wallet.id(),
                wallet.balance(),
                wallet.transactions(),
                updatedCategories
        );

        // Обновляем пользователя с новым кошельком
        User updatedUser = new User(user.id(), user.username(), updatedWallet);
        userManager.updateUser(username, updatedUser);

        logger.info("Category '{}' limit successfully updated to '{}' for user '{}'", categoryName, newLimit, username);
        return true;
    }

    // Удаление категории из бюджета
    public boolean removeCategoryFromBudget(String username, String categoryName) {
        logger.info("Removing category '{}' from budget for user '{}'", categoryName, username);
        Optional<User> userOpt = userManager.getUserByUsername(username);
        if (userOpt.isEmpty()) {
            logger.warn("User '{}' not found, unable to remove category", username);
            return false;
        }

        User user = userOpt.get();
        Wallet wallet = user.wallet();

        // Поиск категории по имени
        Optional<Category> categoryOpt = wallet.budgetCategories().stream()
                .filter(category -> category.name().equals(categoryName))
                .findFirst();

        if (categoryOpt.isEmpty()) {
            logger.warn("Category '{}' not found in the budget for user '{}'", categoryName, username);
            return false; // Категория не найдена
        }

        Category category = categoryOpt.get();
        List<Category> updatedCategories = new ArrayList<>(wallet.budgetCategories());
        updatedCategories.remove(category);

        Wallet updatedWallet = new Wallet(
                wallet.id(),
                wallet.balance(),
                wallet.transactions(),
                updatedCategories
        );

        // Обновляем пользователя с новым кошельком
        User updatedUser = new User(user.id(), user.username(), updatedWallet);
        userManager.updateUser(username, updatedUser);

        logger.info("Category '{}' successfully removed from budget for user '{}'", categoryName, username);
        return true;
    }

    public boolean updateCategory(String username, String oldCategoryName, String newCategoryName,
                                  BigDecimal newLimit) {
        logger.info("Updating category '{}' to '{}' and limit to '{}' for user '{}'", oldCategoryName, newCategoryName,
                newLimit, username);
        Optional<User> userOpt = userManager.getUserByUsername(username);
        if (userOpt.isEmpty()) {
            logger.warn("User '{}' not found, unable to update category", username);
            return false;
        }

        User user = userOpt.get();
        Wallet wallet = user.wallet();

        // Поиск категории по старому имени
        Optional<Category> categoryOpt = wallet.budgetCategories().stream()
                .filter(category -> category.name().equals(oldCategoryName))
                .findFirst();

        if (categoryOpt.isEmpty()) {
            logger.warn("Category '{}' not found in the budget for user '{}'", oldCategoryName, username);
            return false; // Категория не найдена
        }

        Category category = categoryOpt.get();
        Category updatedCategory = new Category(
                category.id(),
                newCategoryName, // Обновляем имя категории
                category.budgetLimit(),
                newLimit, // Обновляем лимит
                wallet
        );
//TODO
        List<Category> updatedCategories = new ArrayList<>(wallet.budgetCategories());
        updatedCategories.set(updatedCategories.indexOf(category), updatedCategory);

        Wallet updatedWallet = new Wallet(
                wallet.id(),
                wallet.balance(),
                wallet.transactions(),
                updatedCategories
        );

        // Обновляем пользователя с новым кошельком
        User updatedUser = new User(user.id(), user.username(), updatedWallet);
        userManager.updateUser(username, updatedUser);

        logger.info("Category '{}' successfully updated to '{}' and limit set to '{}' for user '{}'", oldCategoryName,
                newCategoryName, newLimit, username);
        return true;
    }
}
