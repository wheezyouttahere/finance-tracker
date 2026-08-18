package com.finance.finance_tracker.service;

import com.finance.finance_tracker.dto.TransactionRequestDto;
import com.finance.finance_tracker.dto.TransactionResponseDto;
import com.finance.finance_tracker.entity.CategoryEntity;
import com.finance.finance_tracker.entity.Transaction;
import com.finance.finance_tracker.entity.UserEntity;
import com.finance.finance_tracker.exception.ResourseNotFoundException;
import com.finance.finance_tracker.repository.CategoryRepository;
import com.finance.finance_tracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<TransactionResponseDto> getTransactions(Long userId, Pageable pageable) {
        return transactionRepository.findAllByUserId(userId, pageable)
                .map(this::mapToDto);
    }

    @Transactional
    public TransactionResponseDto createTransaction(TransactionRequestDto dto, UserEntity user) {
        Long categoryId = dto.categoryId(); // или dto.getCategoryId() если DTO — класс
        CategoryEntity category = categoryRepository.findByIdAndUserId(categoryId, user.getId())
                .orElseThrow(() -> new ResourseNotFoundException("Category not found with id: " + categoryId));

        Transaction transaction = Transaction.builder()
                .amount(dto.amount()) // или dto.getAmount()
                .description(dto.description()) // или dto.getDescription()
                .date(dto.date() != null ? dto.date() : LocalDate.now())
                .category(category)
                .user(user)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return mapToDto(saved);
    }

    @Transactional
    public void deleteTransaction(UUID id, Long userId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourseNotFoundException("Transaction not found with id: " + id));

        transactionRepository.delete(transaction);
    }

    private TransactionResponseDto mapToDto(Transaction t) {
        return new TransactionResponseDto(
                t.getId(),
                t.getAmount(),
                t.getCategory().getType(),
                t.getCategory().getId(),
                t.getCategory().getName(),
                t.getDate(),
                t.getDescription()
        );
    }
}