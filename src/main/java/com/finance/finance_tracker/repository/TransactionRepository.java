package com.finance.finance_tracker.repository;

import com.finance.finance_tracker.entity.Transaction;
import com.finance.finance_tracker.entity.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    Page<Transaction> findAllByUserId(Long userId, Pageable pageable);

    Optional<Transaction> findByIdAndUserId(UUID id, Long userId);

    @Query("""
        SELECT t FROM Transaction t 
        WHERE t.user.id = :userId
          AND (:type IS NULL OR t.category.type = :type)
          AND (:startDate IS NULL OR t.date >= :startDate)
          AND (:endDate IS NULL OR t.date <= :endDate)
    """)
    Page<Transaction> findFiltered(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("""
    SELECT COALESCE(SUM(t.amount), 0) 
    FROM Transaction t 
    WHERE t.user.id = :userId 
      AND t.category.type = :type 
      AND t.date >= :startDate 
      AND t.date <= :endDate
""")
    BigDecimal calculateTotalByPeriod(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

}