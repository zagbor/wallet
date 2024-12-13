package com.zagbor.wallet.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.validator.constraints.NotBlank;

public record TransactionDto(String id,
                             @NotBlank(message = "Name cannot be empty")
                             String name,
                             @NotNull(message = "Amount cannot be null")
                             @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
                             BigDecimal amount,
                             @PastOrPresent(message = "Date must be in the past or present")
                             @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
                             LocalDateTime date,
                             @NotBlank(message = "Category cannot be empty")
                             String categoryName) {
}
