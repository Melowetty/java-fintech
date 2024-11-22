package ru.melowetty.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import ru.melowetty.exception.ChangePasswordTokenIsExpired;
import ru.melowetty.exception.ChangePasswordTokenIsNotValidatedException;
import ru.melowetty.exception.EntityNotFoundException;
import ru.melowetty.exception.MaxAttemptsSendCodeReachedException;
import ru.melowetty.exception.WrongAuthCodeException;
import ru.melowetty.model.ChangePasswordToken;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class PasswordRecoveryService {
    private final UserService userService;
    private final HashMap<String, ChangePasswordToken> operations = new HashMap<>();
    private final HashMap<String, String> authCodes = new HashMap<>();
    private final int MAX_ATTEMPTS_COUNT = 3;

    public ChangePasswordToken initPasswordRecovery(String username) {
        if (!userService.usernameIsExist(username)) {
            throw new EntityNotFoundException("Такой пользователь не найден!");
        }

        var currentDate = LocalDateTime.now();
        var token = generateToken();
        var entity = new ChangePasswordToken(
            username,
                token,
                1,
                false,
                currentDate.plus(getAttemptDelta()),
                currentDate.plus(getTokenLiveTime())
        );

        operations.put(token, entity);
        authCodes.put(token, generateAuthCode());

        return entity;
    }

    public ChangePasswordToken reSendAuthCode(String token) {
        validateToken(token);

        var entity = operations.get(token);

        if (entity.nextAttemptTime.isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Ещё нельзя выслать новый код!");
        }

        if (entity.attempts == MAX_ATTEMPTS_COUNT) {
            throw new MaxAttemptsSendCodeReachedException("Достигнут лимит в отправке новых сообщений с кодом!");
        }

        entity.attempts += 1;
        entity.nextAttemptTime = entity.nextAttemptTime.plus(getAttemptDelta());

        authCodes.put(token, generateAuthCode());

        return entity;
    }

    public ChangePasswordToken validateAuthCode(String token, String authCode) {
        validateToken(token);

        var entity = operations.get(token);
        var code = authCodes.get(token);

        if (!authCode.equals(code)) {
            throw new WrongAuthCodeException("Неверный код подтверждения!");
        }

        entity.isValidated = true;

        return entity;
    }

    public void changePassword(String token, String newPassword) {
        validateToken(token);
        var entity = operations.get(token);
        if (!entity.isValidated) {
            throw new ChangePasswordTokenIsNotValidatedException("Сброс пароля еще не подтверждён!");
        }

        var username = entity.username;

        userService.changePassword(username, newPassword);

        inValidateToken(token);
    }

    public void inValidateToken(String token) {
        operations.remove(token);
        authCodes.remove(token);
    }

    public void validateToken(String token) {
        if (!operations.containsKey(token)) {
            throw new ChangePasswordTokenIsExpired("Этот токен для сброса пароля уже недействителен!");
        }
        var entity = operations.get(token);

        if (entity.expirationTime.isBefore(LocalDateTime.now())) {
            inValidateToken(token);
            throw new ChangePasswordTokenIsExpired("Токен для сброса пароля недействителен!");
        }
    }

    private String generateToken() {
        return RandomStringUtils.randomAlphanumeric(32);
    }

    public String generateAuthCode() {
        return "0000";
    }

    private TemporalAmount getAttemptDelta() {
        return Duration.ofMinutes(2);
    }

    private TemporalAmount getTokenLiveTime() {
        return Duration.ofMinutes(10);
    }
}
