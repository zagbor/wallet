package com.zagbor.wallet.controller;

import com.zagbor.wallet.model.Wallet;
import com.zagbor.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/{username}")
    public Wallet getWallet(@PathVariable String username) {
        return walletService.getWalletByUsername(username);  // или выбрасываем исключение, если не найдено
    }
}
