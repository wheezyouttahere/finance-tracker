package com.finance.finance_tracker.repository;

import com.finance.finance_tracker.entity.CategoryEntity;
import com.finance.finance_tracker.entity.TransactionType;
import com.finance.finance_tracker.entity.UserEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
class CategoryRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Успешное сохранение и поиск категорий пользователя в реальной БД")
    void shouldSaveAndFindCategory() {
        // 1. Создаем и сохраняем пользователя в тестовом контейнере
        UserEntity user = new UserEntity();
        user.setUsername("test_user");
        user.setPassword("secret_password");
        user = userRepository.save(user);

        // 2. Создаем и сохраняем категорию
        CategoryEntity category = new CategoryEntity();
        category.setName("Еда");
        category.setType(TransactionType.EXPENSE);
        category.setUser(user);
        categoryRepository.save(category);

        // 3. Проверяем выборку из БД через репозиторий
        List<CategoryEntity> categories = categoryRepository.findAllByUserId(user.getId());

        assertThat(categories).isNotEmpty();
        assertThat(categories).hasSize(1);
        assertThat(categories.get(0).getName()).isEqualTo("Еда");
        assertThat(categories.get(0).getType()).isEqualTo(TransactionType.EXPENSE);
    }
}