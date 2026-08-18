package com.finance.finance_tracker.controller;

import com.finance.finance_tracker.dto.TransactionRequestDto;
import com.finance.finance_tracker.dto.TransactionResponseDto;
import com.finance.finance_tracker.entity.UserEntity;
import com.finance.finance_tracker.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public Page<TransactionResponseDto> getTransactions(
            @AuthenticationPrincipal UserEntity user,
            @ParameterObject Pageable pageable
    ) {
        return transactionService.getTransactions(user.getId(), pageable);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TransactionResponseDto createTransaction(
            @Valid @RequestBody TransactionRequestDto dto,
            @AuthenticationPrincipal UserEntity user
    ) {
        return transactionService.createTransaction(dto, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTransaction(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserEntity user
    ) {
        transactionService.deleteTransaction(id, user.getId());
    }
}