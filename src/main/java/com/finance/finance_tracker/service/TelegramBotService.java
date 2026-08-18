package com.finance.finance_tracker.service;

import com.finance.finance_tracker.dto.SummaryDto;
import com.finance.finance_tracker.entity.CategoryEntity;
import com.finance.finance_tracker.entity.Transaction;
import com.finance.finance_tracker.entity.TransactionType;
import com.finance.finance_tracker.entity.UserEntity;
import com.finance.finance_tracker.repository.CategoryRepository;
import com.finance.finance_tracker.repository.TransactionRepository;
import com.finance.finance_tracker.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramBotService extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final AnalyticsService analyticsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) return;

        String messageText = update.getMessage().getText().trim();
        Long chatId = update.getMessage().getChatId();
        User tgUser = update.getMessage().getFrom();

        UserEntity user = getOrCreateUser(tgUser, chatId);

        if (messageText.startsWith("/start")) {
            sendResponse(chatId, String.format(
                    "Привет, *%s*! Твой личный финансовый трекер готов к работе.\n\n" +
                            "📌 *Доступные команды:*\n" +
                            "• `/spend <сумма> <категория>` — записать расход\n" +
                            "  _Пример:_ `/spend 300 Такси`\n\n" +
                            "• `/income <сумма> <категория>` — записать доход\n" +
                            "  _Пример:_ `/income 150000 Зарплата`\n\n" +
                            "• `/balance` — баланс и аналитика за текущий месяц",
                    user.getUsername()
            ));
        } else if (messageText.equals("/balance")) {
            handleBalance(user, chatId);
        } else if (messageText.startsWith("/spend")) {
            handleSpend(user, chatId, messageText);
        } else if (messageText.startsWith("/income")) {
            handleIncome(user, chatId, messageText);
        } else {
            sendResponse(chatId, "Неизвестная команда. Доступные команды:\n• `/spend 300 Такси`\n• `/income 50000 Зарплата`\n• `/balance`");
        }
    }

    private UserEntity getOrCreateUser(User tgUser, Long chatId) {
        return userRepository.findByTelegramChatId(chatId)
                .orElseGet(() -> {
                    String baseUsername = (tgUser.getUserName() != null && !tgUser.getUserName().isBlank())
                            ? tgUser.getUserName()
                            : "user_" + chatId;

                    String uniqueUsername = baseUsername;
                    int counter = 1;
                    while (userRepository.existsByUsername(uniqueUsername)) {
                        uniqueUsername = baseUsername + "_" + counter++;
                    }

                    UserEntity newUser = UserEntity.builder()
                            .username(uniqueUsername)
                            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .telegramChatId(chatId)
                            .role("ROLE_USER")
                            .build();

                    return userRepository.save(newUser);
                });
    }

    private void handleBalance(UserEntity user, Long chatId) {
        SummaryDto summary = analyticsService.getMonthlySummary(user.getId(), YearMonth.now());
        String msg = String.format(
                "📊 *Сводка за текущий месяц:*\n\n" +
                        " Доходы: %s\n" +
                        " Расходы: %s\n\n" +
                        " *Итоговый баланс:* %s",
                summary.totalIncome(), summary.totalExpense(), summary.balance()
        );
        sendResponse(chatId, msg);
    }

    @Transactional
    public void handleSpend(UserEntity user, Long chatId, String text) {
        try {
            String[] parts = text.split("\\s+", 3);
            if (parts.length < 3) {
                sendResponse(chatId, "Используй: `/spend <сумма> <категория>`\nПример: `/spend 300 Такси`");
                return;
            }

            BigDecimal amount;
            try {
                amount = new BigDecimal(parts[1].replace(",", "."));
            } catch (NumberFormatException e) {
                sendResponse(chatId, "Ошибка: сумма должна быть числом (например: 300 или 250.50).");
                return;
            }

            String categoryName = parts[2].trim();

            CategoryEntity category = categoryRepository.findAllByUserId(user.getId()).stream()
                    .filter(c -> c.getName().equalsIgnoreCase(categoryName) && c.getType() == TransactionType.EXPENSE)
                    .findFirst()
                    .orElseGet(() -> {
                        CategoryEntity newCat = new CategoryEntity();
                        newCat.setName(categoryName);
                        newCat.setType(TransactionType.EXPENSE);
                        newCat.setUser(user);
                        return categoryRepository.save(newCat);
                    });

            Transaction transaction = Transaction.builder()
                    .amount(amount)
                    .type(TransactionType.EXPENSE)
                    .description("Добавлено через Telegram")
                    .date(LocalDate.now())
                    .category(category)
                    .user(user)
                    .build();

            transactionRepository.save(transaction);
            sendResponse(chatId, String.format("Расход на *%s* в категорию *%s* сохранен!", amount, category.getName()));

        } catch (Exception e) {
            log.error("Ошибка при сохранении расхода для пользователя {}", user.getUsername(), e);
            sendResponse(chatId, "Произошла ошибка при сохранении расхода.");
        }
    }

    @Transactional
    public void handleIncome(UserEntity user, Long chatId, String text) {
        try {
            String[] parts = text.split("\\s+", 3);
            if (parts.length < 3) {
                sendResponse(chatId, "Используй: `/income <сумма> <категория>`\nПример: `/income 150000 Зарплата`");
                return;
            }

            BigDecimal amount;
            try {
                amount = new BigDecimal(parts[1].replace(",", "."));
            } catch (NumberFormatException e) {
                sendResponse(chatId, "Ошибка: сумма должна быть числом (например: 50000 или 1500.50).");
                return;
            }

            String categoryName = parts[2].trim();

            CategoryEntity category = categoryRepository.findAllByUserId(user.getId()).stream()
                    .filter(c -> c.getName().equalsIgnoreCase(categoryName) && c.getType() == TransactionType.INCOME)
                    .findFirst()
                    .orElseGet(() -> {
                        CategoryEntity newCat = new CategoryEntity();
                        newCat.setName(categoryName);
                        newCat.setType(TransactionType.INCOME);
                        newCat.setUser(user);
                        return categoryRepository.save(newCat);
                    });

            Transaction transaction = Transaction.builder()
                    .amount(amount)
                    .type(TransactionType.INCOME)
                    .description("Добавлено через Telegram")
                    .date(LocalDate.now())
                    .category(category)
                    .user(user)
                    .build();

            transactionRepository.save(transaction);
            sendResponse(chatId, String.format("💰 Доход на *%s* в категорию *%s* сохранен!", amount, category.getName()));

        } catch (Exception e) {
            log.error("Ошибка при сохранении дохода для пользователя {}", user.getUsername(), e);
            sendResponse(chatId, "Произошла ошибка при сохранении дохода.");
        }
    }

    public void sendResponse(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        message.enableMarkdown(true);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения пользователю {}", chatId, e);
        }
    }
}