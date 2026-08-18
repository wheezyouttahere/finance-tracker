package com.finance.finance_tracker.repository;

import com.finance.finance_tracker.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByTelegramChatId(Long telegramChatId);
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);
}
