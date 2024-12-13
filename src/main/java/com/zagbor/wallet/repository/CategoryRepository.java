package com.zagbor.wallet.repository;

import com.zagbor.wallet.model.Category;
import com.zagbor.wallet.service.CategoryService;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
@Slf4j
@Repository
@AllArgsConstructor
public class CategoryRepository {

    private static final Logger logger = LoggerFactory.getLogger(CategoryRepository.class);

    private UserRepository repository;

    public Category getCategoryByName(String username, String categoryName) {
        return repository.getUserByUsername(username).orElseThrow().getWallet().getBudgetCategories()
                .stream().filter(cat -> cat.getName().equals(categoryName)).findFirst().orElseThrow();
    }

    public void updateCategory(String username, String categoryName, Category category) {
        var user = repository.getUserByUsername(username).orElseThrow();
        var wallet = user.getWallet();
        var categories = wallet.getBudgetCategories();

        var updatedCategories = categories.stream()
                .filter(cat -> !cat.getName().equals(categoryName))
                .collect(Collectors.toList());
        updatedCategories.add(category);

        categories.clear();
        categories.addAll(updatedCategories);
    }

    public void deleteCategory(String username, String categoryName) {
        logger.info("Deleting category '{}' for user '{}'", categoryName, username);
        var user = repository.getUserByUsername(username).orElseThrow();
        var wallet = user.getWallet();
        var categories = wallet.getBudgetCategories();

        var updatedCategories = categories.stream()
                .filter(cat -> !cat.getName().equals(categoryName))
                .toList();

        categories.clear();
        categories.addAll(updatedCategories);
    }

}
