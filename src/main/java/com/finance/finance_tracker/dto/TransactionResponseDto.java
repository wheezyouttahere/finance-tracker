package com.finance.finance_tracker.dto;

import com.finance.finance_tracker.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionResponseDto(
        UUID id,
        BigDecimal amount,
        TransactionType type,
        Long categoryId,
        String categoryName,
        LocalDate date,
        String description
) {}