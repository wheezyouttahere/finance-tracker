package com.finance.finance_tracker.service;

import com.finance.finance_tracker.dto.SummaryDto;
import com.finance.finance_tracker.entity.UserEntity;
import com.finance.finance_tracker.repository.UserRepository;
import com.finance.finance_tracker.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MonthlyReportScheduler {

    private final UserRepository userRepository;
    private final AnalyticsService analyticsService;

    // Срабатывает в 00:00 первого числа каждого месяца (Cron: sec min hour day month ?)
    @Scheduled(cron = "0 0 0 1 * ?")
    public void generateMonthlyReports() {
        YearMonth previousMonth = YearMonth.now().minusMonths(1);
        log.info("Starting monthly report generation for: {}", previousMonth);

        List<UserEntity> users = userRepository.findAll();
        for (UserEntity user : users) {
            SummaryDto summary = analyticsService.getMonthlySummary(user.getId(), previousMonth);
            log.info("User: {} | Expense: {} | Income: {} | Balance: {}",
                    user.getUsername(), summary.totalExpense(), summary.totalIncome(), summary.balance());
        }

        log.info("Monthly reports generated successfully.");
    }
}