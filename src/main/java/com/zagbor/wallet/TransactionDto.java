package com.zagbor.wallet;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import org.hibernate.validator.constraints.NotBlank;

public record TransactionDto(Long id,
                             @NotBlank(message = "Name cannot be empty")
                             String name,
                             String type,
                             @NotNull(message = "Amount cannot be null")
                             @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than zero")
                             BigDecimal amount,
                             @NotNull(message = "Date cannot be null")
                             @PastOrPresent(message = "Date must be in the past or present")
                             LocalDateTime date,
                             @NotBlank(message = "Category cannot be empty")
                             String category) {
}
