package com.finance.finance_tracker.service;

import com.finance.finance_tracker.dto.SummaryDto;
import com.finance.finance_tracker.entity.TransactionType;
import com.finance.finance_tracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final TransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public SummaryDto getMonthlySummary(Long userId, YearMonth month) {
        LocalDate startDate = month.atDay(1);
        LocalDate endDate = month.atEndOfMonth();

        BigDecimal totalExpense = transactionRepository.calculateTotalByPeriod(
                userId, TransactionType.EXPENSE, startDate, endDate
        );

        BigDecimal totalIncome = transactionRepository.calculateTotalByPeriod(
                userId, TransactionType.INCOME, startDate, endDate
        );

        BigDecimal balance = totalIncome.subtract(totalExpense);

        return new SummaryDto(totalExpense, totalIncome, balance);
    }
}