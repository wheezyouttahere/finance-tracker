package com.finance.finance_tracker.service;

import com.finance.finance_tracker.dto.CategoryDto;
import com.finance.finance_tracker.entity.CategoryEntity;
import com.finance.finance_tracker.entity.TransactionType;
import com.finance.finance_tracker.entity.UserEntity;
import com.finance.finance_tracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getAllCategories(Long userId) {
        return categoryRepository.findAllByUserId(userId).stream()
                .map(cat -> new CategoryDto(cat.getId(), cat.getName(), cat.getType().name()))
                .toList();
    }

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryDto dto, UserEntity user) {
        CategoryEntity entity = new CategoryEntity();
        entity.setName(dto.getName());
        entity.setType(TransactionType.valueOf(dto.getType()));
        entity.setUser(user);

        CategoryEntity saved = categoryRepository.save(entity);
        return new CategoryDto(saved.getId(), saved.getName(), saved.getType().name());
    }
}