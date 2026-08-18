package com.finance.finance_tracker.service;

import com.finance.finance_tracker.dto.TransactionRequestDto;
import com.finance.finance_tracker.dto.TransactionResponseDto;
import com.finance.finance_tracker.entity.CategoryEntity;
import com.finance.finance_tracker.entity.Transaction;
import com.finance.finance_tracker.entity.TransactionType;
import com.finance.finance_tracker.entity.UserEntity;
import com.finance.finance_tracker.repository.CategoryRepository;
import com.finance.finance_tracker.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TransactionService transactionService;

    private UserEntity user;
    private CategoryEntity category;

    @BeforeEach
    void setUp() {
        user = new UserEntity();
        user.setId(1L);
        user.setUsername("beks");

        category = new CategoryEntity();
        category.setId(10L);
        category.setName("Еда");
        category.setType(TransactionType.EXPENSE);
        category.setUser(user);
    }

    @Test
    @DisplayName("Успешное создание транзакции")
    void createTransaction_Success() {
        TransactionRequestDto request = new TransactionRequestDto(
                new BigDecimal("1500.00"),
                TransactionType.EXPENSE,
                10L,
                LocalDate.now(),
                "Обед"
        );

        // Мокаем любые варианты поиска категории
        when(categoryRepository.findById(any())).thenReturn(Optional.of(category));
        when(categoryRepository.findByIdAndUserId(any(), any())).thenReturn(Optional.of(category));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction saved = invocation.getArgument(0);
            saved.setId(UUID.randomUUID());
            return saved;
        });

        TransactionResponseDto response = transactionService.createTransaction(request, user);

        assertThat(response).isNotNull();
        assertThat(response.amount()).isEqualByComparingTo("1500.00");
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Ошибка создания транзакции, если категория не найдена")
    void createTransaction_CategoryNotFound_ThrowsException() {
        TransactionRequestDto request = new TransactionRequestDto(
                new BigDecimal("500.00"),
                TransactionType.EXPENSE,
                999L,
                LocalDate.now(),
                "Тест"
        );

        when(categoryRepository.findById(any())).thenReturn(Optional.empty());
        when(categoryRepository.findByIdAndUserId(any(), any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.createTransaction(request, user))
                .isInstanceOf(RuntimeException.class);

        verify(transactionRepository, never()).save(any(Transaction.class));
    }
}