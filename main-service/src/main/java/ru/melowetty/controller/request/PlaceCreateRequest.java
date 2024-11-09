package ru.melowetty.controller.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@Generated
@NoArgsConstructor
@AllArgsConstructor
public class PlaceCreateRequest {
    @NotNull(message = "Не указан код города")
    @Length(min = 3, message = "Длина кода города не может быть меньше 3-х символов")
    @NotBlank(message = "Код города не может быть пустым")
    public String slug;

    @NotNull(message = "Не указано название города")
    @Length(min = 3, message = "Длина названия города не может быть меньше 3-х символов")
    @NotBlank(message = "Название города не может быть пустым")
    public String name;
}
