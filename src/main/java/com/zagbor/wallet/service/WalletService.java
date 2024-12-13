package com.zagbor.wallet.service;

import com.zagbor.wallet.model.*;
import java.util.ArrayList;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class WalletService {

    private static final Logger logger = LoggerFactory.getLogger(WalletService.class);

    private final UserService userService;

    // Получение кошелька пользователя по имени
    public Wallet getWalletByUsername(String username) {
        logger.info("Fetching wallet for user '{}'", username);
        return userService.getUserByUsername(username).getWallet();
    }
}
