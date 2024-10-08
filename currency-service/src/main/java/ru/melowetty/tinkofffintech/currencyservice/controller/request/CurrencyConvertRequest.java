package ru.melowetty.tinkofffintech.currencyservice.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Currency;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на конвертацию валюты")
public class CurrencyConvertRequest {
    @Schema(description = "Из какой валюты конвертировать в виде кода", example = "USD")
    @NotNull
    @Length(min = 3, max = 3, message = "Длина кода валюты должна быть равна 3")
    public String fromCurrency;

    @Schema(description = "В какую валюту конвертировать в виде кода", example = "RUB")
    @NotNull
    @Length(min = 3, max = 3, message = "Длина кода валюты должна быть равна 3")
    public String toCurrency;

    @Schema(description = "Сумма изначальной валюты", example = "100.50")
    @NotNull
    public float amount;

    @AssertTrue(message = "Такой исходной валюты не существует")
    public boolean checkFromCurrency() {
        return checkCurrency(fromCurrency);
    }

    @AssertTrue(message = "Такой целевой валюты не существует")
    public boolean checkToCurrency() {
        return checkCurrency(toCurrency);
    }

    @AssertTrue(message = "Сумма для конвертации должна быть больше 0!")
    public boolean checkAmount() {
        return amount > 0;
    }

    public boolean checkCurrency(String targetCurrency) {
        try {
            var currency = Currency.getInstance(targetCurrency);
            return currency != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
