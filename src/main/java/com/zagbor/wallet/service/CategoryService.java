package com.zagbor.wallet.service;

import com.zagbor.wallet.model.*;
import com.zagbor.wallet.repository.CategoryRepository;
import java.util.UUID;
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
public class CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);

    private final UserService userService;

    private final CategoryRepository categoryRepository;

    // Получение списка категорий бюджета пользователя
    public List<Category> getBudgetCategories(String username) {
        logger.info("Fetching budget categories for user '{}'", username);
        User user = userService.getUserByUsername(username);
        return user.getWallet().getBudgetCategories();
    }

    // Добавление категории в бюджет пользователя
    public boolean addCategoryToBudget(String username, String categoryName, BigDecimal budgetLimit,
                                       TransactionType type) {
        logger.info("Adding category '{}' to budget for user '{}'", categoryName, username);
        User user = userService.getUserByUsername(username);
        Wallet wallet = user.getWallet();

        // Проверка на существование категории
        Optional<Category> existingCategoryOpt = wallet.getBudgetCategories().stream()
                .filter(category -> category.getName().equals(categoryName)).findFirst();

        if (existingCategoryOpt.isPresent()) {
            logger.warn("Category '{}' already exists in the budget for user '{}'", categoryName, username);
            throw new IllegalArgumentException("Category already exists in the budget for user");
        }

        // Создаем новую категорию
        Category newCategory = new Category(UUID.randomUUID().toString(), // ID категории
                categoryName, budgetLimit, BigDecimal.ZERO, false, new ArrayList<>(), type);

        List<Category> updatedCategories = new ArrayList<>(wallet.getBudgetCategories());
        updatedCategories.add(newCategory);

        Wallet updatedWallet = new Wallet(updatedCategories);

        // Обновляем пользователя с новым кошельком
        User updatedUser = new User(user.getUsername(), updatedWallet);
        userService.updateUser(username, updatedUser);

        logger.info("Category '{}' successfully added to budget for user '{}'", categoryName, username);
        return true;
    }


    public void removeCategoryFromBudget(String username, String categoryName) {
        logger.info("Removing category '{}' from budget for user '{}'", categoryName, username);

        List<Transaction> transactions = categoryRepository.getCategoryByName(username, categoryName).getTransactions();

        if (!transactions.isEmpty()) {
            logger.warn("Category '{}' contains transactions, cannot be removed", categoryName);
            throw new IllegalArgumentException("Category contains transactions, cannot be removed");
        }

        categoryRepository.deleteCategory(username, categoryName);

        logger.info("Category '{}' successfully removed from budget for user '{}'", categoryName, username);
    }

    public boolean updateCategory(String username, String oldCategoryName, String newCategoryName,
                                  BigDecimal newLimit) {
        logger.info("Updating category '{}' to '{}' and limit to '{}' for user '{}'", oldCategoryName, newCategoryName,
                newLimit, username);
        User user = userService.getUserByUsername(username);

        Wallet wallet = user.getWallet();

        // Поиск категории по старому имени
        Optional<Category> categoryOpt = wallet.getBudgetCategories().stream()
                .filter(category -> category.getName().equals(oldCategoryName)).findFirst();

        if (categoryOpt.isEmpty()) {
            logger.warn("Category '{}' not found in the budget for user '{}'", oldCategoryName, username);
            return false; // Категория не найдена
        }

        Category category = categoryOpt.get();
        Category updatedCategory = new Category(category.getCategoryId(), newCategoryName,
                newLimit, category.getSpent(), isExcess(newLimit, category.getSpent()), category.getTransactions(),
                category.getType());
        List<Category> updatedCategories = new ArrayList<>(wallet.getBudgetCategories());
        updatedCategories.set(updatedCategories.indexOf(category), updatedCategory);

        Wallet updatedWallet = new Wallet(updatedCategories);

        // Обновляем пользователя с новым кошельком
        User updatedUser = new User(user.getUsername(), updatedWallet);
        userService.updateUser(username, updatedUser);

        logger.info("Category '{}' successfully updated to '{}' and limit set to '{}' for user '{}'", oldCategoryName,
                newCategoryName, newLimit, username);
        return true;
    }

    public void updateCategorySpent(String username, String categoryName, BigDecimal amount, boolean isDelete) {
        Category category = categoryRepository.getCategoryByName(username, categoryName);

        if (category.getType().equals(TransactionType.INCOME)) {
            return;
        }

        BigDecimal updatedSpent = calculateUpdatedSpent(category.getSpent(), amount, isDelete);
        boolean isExcess = isExcess(category.getBudgetLimit(), updatedSpent);

        Category updatedCategory = new Category(
                category.getCategoryId(),
                category.getName(),
                category.getBudgetLimit(),
                updatedSpent,
                isExcess,
                category.getTransactions(),
                category.getType()
        );

        categoryRepository.updateCategory(username, categoryName, updatedCategory);
    }

    public Category getCategoryByName(String username, String categoryName) {
        return categoryRepository.getCategoryByName(username, categoryName);
    }

    private boolean isExcess(BigDecimal budgetLimit, BigDecimal spent) {
        return budgetLimit.compareTo(spent) < 0;
    }

    private BigDecimal calculateUpdatedSpent(BigDecimal currentSpent, BigDecimal amount, boolean isDelete) {
        return currentSpent = isDelete ? currentSpent.subtract(amount) : currentSpent.add(amount);
    }

}

