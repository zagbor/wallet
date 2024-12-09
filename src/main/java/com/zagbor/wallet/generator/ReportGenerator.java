package com.zagbor.wallet.generator;

import com.zagbor.wallet.manager.BudgetManager;
import com.zagbor.wallet.manager.TransactionManager;
import com.zagbor.wallet.manager.UserManager;
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

    private final UserManager userManager;
    private final TransactionManager transactionManager;
    private final BudgetManager budgetManager;

    // Генерация отчета по транзакциям пользователя
    public String generateTransactionReport(String username) {
        logger.info("Generating transaction report for user '{}'", username);
        Optional<User> userOpt = userManager.getUserByUsername(username);
        if (userOpt.isEmpty()) {
            logger.warn("User '{}' not found, unable to generate transaction report", username);
            return "User not found";
        }

        User user = userOpt.get();
        List<Transaction> transactions = transactionManager.getTransactions(username);

        // Формируем отчет
        StringBuilder report = new StringBuilder();
        report.append("Transaction Report for User: ").append(username).append("\n");
        report.append("Date, Type, Amount, Category\n");

        for (Transaction transaction : transactions) {
            report.append(transaction.date()).append(", ")
                    .append(transaction.type()).append(", ")
                    .append(transaction.amount()).append(", ")
                    .append(transaction.category().name()).append("\n");
        }

        logger.info("Transaction report generated for user '{}'", username);
        return report.toString();
    }

    // Генерация отчета по бюджету пользователя
    public String generateBudgetReport(String username) {
        logger.info("Generating budget report for user '{}'", username);
        Optional<User> userOpt = userManager.getUserByUsername(username);
        if (userOpt.isEmpty()) {
            logger.warn("User '{}' not found, unable to generate budget report", username);
            return "User not found";
        }

        User user = userOpt.get();
        List<Category> categories = budgetManager.getBudgetCategories(username);

        // Формируем отчет
        StringBuilder report = new StringBuilder();
        report.append("Budget Report for User: ").append(username).append("\n");
        report.append("Category, Budget Limit, Current Limit\n");

        for (Category category : categories) {
            report.append(category.name()).append(", ")
                    .append(category.budgetLimit()).append(", ")
                    .append(category.currentLimit()).append("\n");
        }

        logger.info("Budget report generated for user '{}'", username);
        return report.toString();
    }

    // Сохранение отчета о транзакциях в CSV файл
    public boolean saveTransactionReportToFile(String username, String filePath) {
        logger.info("Saving transaction report to file for user '{}'", username);
        String report = generateTransactionReport(username);
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(report);
            logger.info("Transaction report saved to file '{}'", filePath);
            return true;
        } catch (IOException e) {
            logger.error("Error saving transaction report to file", e);
            return false;
        }
    }

    // Сохранение отчета о бюджете в CSV файл
    public boolean saveBudgetReportToFile(String username, String filePath) {
        logger.info("Saving budget report to file for user '{}'", username);
        String report = generateBudgetReport(username);
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(report);
            logger.info("Budget report saved to file '{}'", filePath);
            return true;
        } catch (IOException e) {
            logger.error("Error saving budget report to file", e);
            return false;
        }
    }

    // Генерация общего отчета с транзакциями и бюджетом
    public String generateFullReport(String username) {
        String transactionReport = generateTransactionReport(username);
        String budgetReport = generateBudgetReport(username);

        return transactionReport + "\n\n" + budgetReport;
    }
}
