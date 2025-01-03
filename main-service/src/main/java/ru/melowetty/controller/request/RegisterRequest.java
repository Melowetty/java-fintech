package ru.melowetty.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.melowetty.validation.annotation.ValidPassword;

@Data
@Generated
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    @Length(max = 255)
    @NotBlank(message = "Имя пользователя не может быть пустым")
    String username;

    @ValidPassword
    String password;
}