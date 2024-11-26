package ru.melowetty.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import ru.melowetty.validation.annotation.ValidPassword;

@Data
@AllArgsConstructor
public class LoginRequest {
    @Length(min = 3, max = 255)
    @NotBlank(message = "Имя пользователя не может быть пустым")
    String username;

    @ValidPassword
    String password;

    @NotNull(message = "Поле запомнить меня не должно быть пустым")
    boolean rememberMe;
}
