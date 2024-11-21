package ru.melowetty.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Generated
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @Length(max = 255)
    @NotBlank(message = "Имя пользователя не может быть пустым")
    String username;

    @Length(max = 255)
    @NotBlank(message = "Пароль не может быть пустым")
    String password;
}