package com.finance.finance_tracker.dto;

import java.math.BigDecimal;

public record TransactionSummaryDto(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance
) {}