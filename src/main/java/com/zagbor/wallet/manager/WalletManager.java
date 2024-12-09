package com.zagbor.wallet.manager;

import com.zagbor.wallet.model.*;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class WalletManager {

    private static final Logger logger = LoggerFactory.getLogger(WalletManager.class);

    private final UserManager userManager;

    // Получение кошелька пользователя по имени
    public Optional<Wallet> getWalletByUsername(String username) {
        logger.info("Fetching wallet for user '{}'", username);
        Optional<User> userOpt = userManager.getUserByUsername(username);
        return userOpt.map(User::wallet);
    }

    // Создание кошелька для пользователя
    public boolean createWalletForUser(String username) {
        logger.info("Creating wallet for user '{}'", username);
        Optional<User> userOpt = userManager.getUserByUsername(username);
        if (userOpt.isEmpty()) {
            logger.warn("User '{}' not found, unable to create wallet", username);
            return false;
        }

        User user = userOpt.get();
        Wallet wallet = new Wallet(
                0L, // ID будет сгенерирован, если используете базу данных
                BigDecimal.ZERO, // Начальный баланс
                new ArrayList<>(), // Начальные транзакции пусты
                new ArrayList<>() // Начальные категории пусты
        );

        // Обновляем пользователя с новым кошельком
        User updatedUser = new User(user.id(), user.username(), wallet);
        userManager.updateUser(username, updatedUser);

        logger.info("Wallet successfully created for user '{}'", username);
        return true;
    }

    // Обновление кошелька пользователя
    public boolean updateWallet(String username, Wallet updatedWallet) {
        logger.info("Updating wallet for user '{}'", username);
        Optional<User> userOpt = userManager.getUserByUsername(username);
        if (userOpt.isEmpty()) {
            logger.warn("User '{}' not found, unable to update wallet", username);
            return false;
        }

        User user = userOpt.get();
        User updatedUser = new User(user.id(), user.username(), updatedWallet);
        userManager.updateUser(username, updatedUser);

        logger.info("Wallet successfully updated for user '{}'", username);
        return true;
    }

    // Пополнение кошелька пользователя
    public boolean deposit(String username, BigDecimal amount) {
        logger.info("Depositing amount '{}' into wallet of user '{}'", amount, username);
        Optional<Wallet> walletOpt = getWalletByUsername(username);
        if (walletOpt.isEmpty()) {
            logger.warn("Wallet for user '{}' not found", username);
            return false;
        }

        Wallet wallet = walletOpt.get();
        BigDecimal updatedBalance = wallet.balance().add(amount);

        Wallet updatedWallet = new Wallet(
                wallet.id(),
                updatedBalance,
                wallet.transactions(),
                wallet.budgetCategories()
        );

        return updateWallet(username, updatedWallet);
    }

    // Снятие средств с кошелька пользователя
    public boolean withdraw(String username, BigDecimal amount) {
        logger.info("Withdrawing amount '{}' from wallet of user '{}'", amount, username);
        Optional<Wallet> walletOpt = getWalletByUsername(username);
        if (walletOpt.isEmpty()) {
            logger.warn("Wallet for user '{}' not found", username);
            return false;
        }

        Wallet wallet = walletOpt.get();
        if (wallet.balance().compareTo(amount) < 0) {
            logger.warn("Insufficient balance for user '{}'. Available balance: '{}'", username, wallet.balance());
            return false; // Недостаточно средств
        }

        BigDecimal updatedBalance = wallet.balance().subtract(amount);

        Wallet updatedWallet = new Wallet(
                wallet.id(),
                updatedBalance,
                wallet.transactions(),
                wallet.budgetCategories()
        );

        return updateWallet(username, updatedWallet);
    }
}
