package com.zagbor.wallet.controller;

import com.zagbor.wallet.model.Category;
import com.zagbor.wallet.manager.BudgetManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final BudgetManager budgetManager;

    @GetMapping
    public List<Category> getCategories() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return budgetManager.getBudgetCategories(authentication.getName());
    }

    @PostMapping("/{username}")
    @ResponseStatus(HttpStatus.CREATED)
    public boolean addCategory(@PathVariable String username, @RequestBody Category category) {
        return budgetManager.addCategoryToBudget(username, category.name(), category.budgetLimit());
    }

    @PutMapping("/{username}/{categoryId}")
    public boolean updateCategory(@PathVariable String username, @PathVariable String categoryName,
                                  @RequestBody Category category) {
        return budgetManager.updateCategory(username, categoryName, category.name(), category.budgetLimit());
    }

    @DeleteMapping("/{username}/{categoryName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable String username, @PathVariable String categoryName) {
        budgetManager.removeCategoryFromBudget(username, categoryName);
    }
}
