package ru.melowetty.tinkofffintech.currencyservice.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Currency;

@Data
@AllArgsConstructor
@Schema(description = "Ответ на запрос курса валюты")
public class CurrencyRateResponse {
    @Schema(description = "Валюта, у которой узнаем курс", example = "USD")
    public Currency currency;

    @Schema(description = "Курс валюты по отношению к рублю", example = "10.5")
    public float rate;
}
