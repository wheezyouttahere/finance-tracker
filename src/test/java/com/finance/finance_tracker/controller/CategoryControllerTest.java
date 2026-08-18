package com.finance.finance_tracker.controller;

import com.finance.finance_tracker.dto.CategoryDto;
import com.finance.finance_tracker.entity.TransactionType;
import com.finance.finance_tracker.entity.UserEntity;
import com.finance.finance_tracker.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private CategoryController categoryController;

    private UserEntity testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserEntity();
        testUser.setId(1L);
        testUser.setUsername("beks");

        // Резолвер подставляет testUser вместо @AuthenticationPrincipal
        HandlerMethodArgumentResolver authResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.getParameterType().equals(UserEntity.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter,
                                          ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest,
                                          WebDataBinderFactory binderFactory) {
                return testUser;
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(categoryController)
                .setCustomArgumentResolvers(authResolver)
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/categories — успешный возврат списка категорий")
    void shouldReturnCategories() throws Exception {
        CategoryDto category = new CategoryDto(10L, "Еда", "EXPENSE");

        when(categoryService.getAllCategories(anyLong())).thenReturn(List.of(category));

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].name").value("Еда"));
    }
}