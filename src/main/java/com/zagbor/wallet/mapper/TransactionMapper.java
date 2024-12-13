package com.zagbor.wallet.mapper;

import com.zagbor.wallet.dto.TransactionDto;
import com.zagbor.wallet.model.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {


    TransactionDto toDto(Transaction transaction);

    Transaction toEntity(TransactionDto transactionDto);


    }