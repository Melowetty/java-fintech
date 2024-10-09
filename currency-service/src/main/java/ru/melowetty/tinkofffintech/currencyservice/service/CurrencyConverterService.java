package ru.melowetty.tinkofffintech.currencyservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

@Service
@AllArgsConstructor
public class CurrencyConverterService {
    private final CurrencyService currencyService;

    public BigDecimal convertCurrency(Currency fromCurrency, Currency toCurrency, BigDecimal initialAmount) {
        var fromCurrencyRate = currencyService.getCurrencyRate(fromCurrency);
        var initialAmountByRubles = fromCurrencyRate.multiply(initialAmount);

        var toCurrencyRate = currencyService.getCurrencyRate(toCurrency);
        return initialAmountByRubles.divide(toCurrencyRate, RoundingMode.FLOOR);
    }
}
