package com.finance.finance_tracker.controller;

import com.finance.finance_tracker.dto.SummaryDto;
import com.finance.finance_tracker.entity.UserEntity;
import com.finance.finance_tracker.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Endpoints for financial summary and calculations")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/summary")
    @Operation(summary = "Get monthly financial summary (income, expense, balance)")
    public SummaryDto getSummary(
            @AuthenticationPrincipal UserEntity user,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") YearMonth month
    ) {
        YearMonth targetMonth = (month != null) ? month : YearMonth.now();
        return analyticsService.getMonthlySummary(user.getId(), targetMonth);
    }
}