package com.finance.finance_tracker.service;

import com.finance.finance_tracker.dto.CategoryDto;
import com.finance.finance_tracker.entity.UserEntity;

import java.util.List;

public interface CategoryService {
    List<CategoryDto> getAllCategories(Long userId);
    CategoryDto createCategory(CategoryDto dto, UserEntity user);
}