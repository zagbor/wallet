package com.zagbor.wallet.controller;

import com.zagbor.wallet.model.Wallet;
import com.zagbor.wallet.manager.WalletManager;
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

    private final WalletManager walletManager;

    @GetMapping("/{username}")
    public Wallet getWallet(@PathVariable String username) {
        return walletManager.getWalletByUsername(username).orElse(null);  // или выбрасываем исключение, если не найдено
    }

    @PutMapping("/{username}")
    public boolean updateWallet(@PathVariable String username, @RequestBody Wallet wallet) {
        return walletManager.updateWallet(username, wallet);
    }
}
