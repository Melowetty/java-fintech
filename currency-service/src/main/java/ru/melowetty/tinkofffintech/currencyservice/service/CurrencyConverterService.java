package ru.melowetty.tinkofffintech.currencyservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

@Service
@AllArgsConstructor
public class CurrencyConverterService {
    private final CurrencyService currencyService;

    public BigDecimal convertCurrency(Currency fromCurrency, Currency toCurrency, BigDecimal initialAmount) {
        var fromCurrencyRate = BigDecimal.ONE;
        if (!Objects.equals(fromCurrency.getCurrencyCode(), "RUB")) {
            fromCurrencyRate = currencyService.getCurrencyRate(fromCurrency);
        }

        var initialAmountByRubles = fromCurrencyRate.multiply(initialAmount);

        var toCurrencyRate = BigDecimal.ONE;

        if (!Objects.equals(toCurrency.getCurrencyCode(), "RUB")) {
            toCurrencyRate = currencyService.getCurrencyRate(toCurrency);
        }

        return initialAmountByRubles.divide(toCurrencyRate, 2, RoundingMode.FLOOR);
    }
}
