package ru.melowetty.tinkofffintech.currencyservice.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на конвертацию валюты")
public class CurrencyConvertRequest {
    @Schema(description = "Из какой валюты конвертировать", example = "USD")
    public String fromCurrency;

    @Schema(description = "В какую валюту конвертировать", example = "RUB")
    public String toCurrency;

    @Schema(description = "Сумма изначальной валюты", example = "100.50")
    public float amount;
}
