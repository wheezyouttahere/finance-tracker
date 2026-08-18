package com.finance.finance_tracker.controller;

import com.finance.finance_tracker.dto.CategoryDto;
import com.finance.finance_tracker.entity.UserEntity;
import com.finance.finance_tracker.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getAllCategories(@AuthenticationPrincipal UserEntity user) {
        return categoryService.getAllCategories(user.getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(
            @Valid @RequestBody CategoryDto dto,
            @AuthenticationPrincipal UserEntity user
    ) {
        return categoryService.createCategory(dto, user);
    }
}