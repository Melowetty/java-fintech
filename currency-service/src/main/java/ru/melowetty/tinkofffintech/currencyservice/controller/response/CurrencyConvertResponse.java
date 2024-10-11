package ru.melowetty.tinkofffintech.currencyservice.controller.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@AllArgsConstructor
@Schema(description = "Ответ на конвертацию валюты")
public class CurrencyConvertResponse {
    @Schema(description = "Из какой валюты сконвертировано", example = "USD")
    public Currency fromCurrency;

    @Schema(description = "В какой валюту сконвертировано", example = "RUB")
    public Currency toCurrency;

    @Schema(description = "Сконвертированная сумма в итоговой валюте", example = "10.1")
    public BigDecimal convertedAmount;
}
