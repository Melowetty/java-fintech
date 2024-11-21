package ru.melowetty.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record LoginRequest(
        @Length(max = 255)
        @NotBlank(message = "Имя пользователя не может быть пустым")
        String username,

        @Length(max = 255)
        @NotBlank(message = "Пароль не может быть пустым")
        String password,

        @NotNull(message = "Поле запомнить меня не должно быть пустым")
        boolean rememberMe
) {
}
