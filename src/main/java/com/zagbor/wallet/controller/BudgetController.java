package com.zagbor.wallet.controller;

import com.zagbor.wallet.dto.BudgetDto;
import com.zagbor.wallet.service.BudgetService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budget")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @GetMapping
    public BudgetDto getBudget() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return budgetService.createBudget(authentication.getName());
    }


}
