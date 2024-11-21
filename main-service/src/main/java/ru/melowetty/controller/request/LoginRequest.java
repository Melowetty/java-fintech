package ru.melowetty.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class LoginRequest {
        @Length(min = 3, max = 255)
        @NotBlank(message = "Имя пользователя не может быть пустым")
        String username;

        @Length(min = 3, max = 255)
        @NotBlank(message = "Пароль не может быть пустым")
        String password;

        @NotNull(message = "Поле запомнить меня не должно быть пустым")
        boolean rememberMe;
}
