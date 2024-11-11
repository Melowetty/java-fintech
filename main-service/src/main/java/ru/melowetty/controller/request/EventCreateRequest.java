package ru.melowetty.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class EventCreateRequest {
    @NotNull(message = "Не указано название мероприятия")
    @Length(min = 3, message = "Длина названия мероприятия не может быть меньше 3-х символов")
    @NotBlank(message = "Название мероприятия не может быть пустым")
    public String name;

    @NotNull(message = "Не указана дата проведения мероприятия")
    public LocalDateTime date;

    @NotNull(message = "Не указано место проведения мероприятия")
    @Min(value = 1, message = "ID места проведения должен быть больше 0")
    public Long placeId;
}
