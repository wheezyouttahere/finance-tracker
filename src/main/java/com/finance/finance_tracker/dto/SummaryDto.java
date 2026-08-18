package com.finance.finance_tracker.dto;

import java.math.BigDecimal;

public record SummaryDto(
        BigDecimal totalExpense,
        BigDecimal totalIncome,
        BigDecimal balance
) {}