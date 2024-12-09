package com.zagbor.wallet.mapper;

import com.zagbor.wallet.TransactionDto;
import com.zagbor.wallet.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(source = "category.name", target = "category")  // Преобразуем category в строку
    TransactionDto toDto(Transaction transaction);

    @Mapping(source = "category", target = "category.name")  // Преобразуем строку обратно в category (если нужно)
    Transaction toEntity(TransactionDto transactionDto);
}