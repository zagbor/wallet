package com.zagbor.wallet.model;

public record User(
        Long id,
        String username,
        Wallet wallet
) {}