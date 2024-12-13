package com.zagbor.wallet.controller;

import com.zagbor.wallet.dto.CategoryDto;
import com.zagbor.wallet.mapper.CategoryMapper;
import com.zagbor.wallet.model.TransactionType;
import com.zagbor.wallet.service.CategoryService;
import jakarta.validation.Valid;
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

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    public List<CategoryDto> getCategories() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return categoryService.getBudgetCategories(authentication.getName()).stream()
                .map(categoryMapper::toDto).toList();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public boolean addCategory(@RequestBody @Valid CategoryDto category) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return categoryService.addCategoryToBudget(authentication.getName(), category.name(), category.budgetLimit(),
                TransactionType.valueOf(category.type()));
    }

    @PutMapping("/{categoryName}")
    public boolean updateCategory(@PathVariable String categoryName,
                                  @RequestBody @Valid CategoryDto category) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return categoryService.updateCategory(authentication.getName(), categoryName, category.name(),
                category.budgetLimit());
    }

    @DeleteMapping("/{categoryName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable String categoryName) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        categoryService.removeCategoryFromBudget(authentication.getName(), categoryName);
    }
}
