package com.zagbor.wallet.generator;

import com.zagbor.wallet.service.CategoryService;
import com.zagbor.wallet.service.TransactionService;
import com.zagbor.wallet.service.UserService;
import com.zagbor.wallet.model.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ReportGenerator {

    private static final Logger logger = LoggerFactory.getLogger(ReportGenerator.class);

    private final CategoryService categoryService;


    // Генерация отчета по бюджету пользователя
    public String generateBudgetReport(String username) {
        logger.info("Generating budget report for user '{}'", username);
        List<Category> categories = categoryService.getBudgetCategories(username);

        // Формируем отчет
        StringBuilder report = new StringBuilder();
        report.append("Budget Report for User: ").append(username).append("\n");
        report.append("Category, Budget Limit, Current Limit\n");

        for (Category category : categories) {
            report.append(category.getName()).append(", ")
                    .append(category.getBudgetLimit()).append(", ")
                    .append(category.getSpent()).append("\n");
        }

        logger.info("Budget report generated for user '{}'", username);
        return report.toString();
    }


}
