package com.zagbor.wallet.controller;

import com.zagbor.wallet.dto.TransactionDto;
import com.zagbor.wallet.service.TransactionService;
import com.zagbor.wallet.mapper.TransactionMapper;
import com.zagbor.wallet.model.Transaction;
import com.zagbor.wallet.model.TransactionType;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionMapper transactionMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public boolean addTransaction(@RequestBody @Valid TransactionDto transactionDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return transactionService.addTransaction(authentication.getName(), transactionDto.name(),
                transactionDto.amount(), transactionDto.categoryName());
    }

    @GetMapping
    public List<TransactionDto> getTransactions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        List<Transaction> transactions = transactionService.getTransactions(authentication.getName());
        return (transactions.stream().map(transactionMapper::toDto).toList());
    }

    @DeleteMapping("/{transactionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public boolean removeTransaction(@PathVariable String transactionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return transactionService.removeTransaction(authentication.getName(), transactionId);
    }
}
