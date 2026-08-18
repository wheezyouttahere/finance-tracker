package com.finance.finance_tracker.dto;

import com.finance.finance_tracker.entity.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionRequestDto(
        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        @NotNull(message = "Transaction type is required (INCOME or EXPENSE)")
        TransactionType type,

        @NotNull(message = "Category ID is required")
        Long categoryId,

        @PastOrPresent(message = "Transaction date cannot be in the future")
        LocalDate date,

        String description
) {}