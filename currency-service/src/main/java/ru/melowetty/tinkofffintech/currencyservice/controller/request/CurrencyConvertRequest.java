package ru.melowetty.tinkofffintech.currencyservice.controller.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import ru.melowetty.tinkofffintech.currencyservice.util.CurrencyUtils;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Запрос на конвертацию валюты")
public class CurrencyConvertRequest {
    @Schema(description = "Из какой валюты конвертировать в виде кода", example = "USD")
    @NotNull(message = "Поле не должно быть пустым")
    @Length(min = 3, max = 3, message = "Длина кода валюты должна быть равна 3")
    public String fromCurrency;

    @Schema(description = "В какую валюту конвертировать в виде кода", example = "RUB")
    @NotNull(message = "Поле не должно быть пустым")
    @Length(min = 3, max = 3, message = "Длина кода валюты должна быть равна 3")
    public String toCurrency;

    @Schema(description = "Сумма изначальной валюты", example = "100.50")
    @NotNull(message = "Поле не должно быть пустым")
    public BigDecimal amount;

    @AssertTrue(message = "Такой исходной валюты не существует")
    public boolean isFromCurrency() {
        return fromCurrency != null && CurrencyUtils.getCurrency(fromCurrency) != null;
    }

    @AssertTrue(message = "Такой целевой валюты не существует")
    public boolean isToCurrency() {
        return toCurrency != null && CurrencyUtils.getCurrency(toCurrency) != null;
    }

    @AssertTrue(message = "Сумма для конвертации должна быть больше 0!")
    public boolean isAmount() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
