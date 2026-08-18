package com.finance.finance_tracker.repository;

import com.finance.finance_tracker.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {
    List<CategoryEntity> findAllByUserId(Long userId);
    Optional<CategoryEntity> findByIdAndUserId(Long id, Long userId);
}