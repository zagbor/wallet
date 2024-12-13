package com.zagbor.wallet.repository;

import com.zagbor.wallet.model.User;
import com.zagbor.wallet.model.Wallet;
import com.zagbor.wallet.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class WalletRepository {

    private UserService service;

    public Wallet getWallet(String username) {
        return service.getUserByUsername(username).getWallet();
    }

    public void updateWallet(String username, Wallet wallet) {
        User user = service.getUserByUsername(username);
        User updatedUser = new User(user.getUsername(), wallet);

    }

}
