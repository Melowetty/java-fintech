package ru.melowetty.tinkofffintech.currencyservice.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;

@Service
public class CurrencyConverterService {
    private final CurrencyService currencyService;

    public CurrencyConverterService(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    public BigDecimal convertCurrency(Currency fromCurrency, Currency toCurrency, BigDecimal initialAmount) {
        var fromCurrencyRate = currencyService.getCurrencyRate(fromCurrency);
        var initialAmountByRubles = fromCurrencyRate.multiply(initialAmount);

        var toCurrencyRate = currencyService.getCurrencyRate(toCurrency);
        return initialAmountByRubles.divide(toCurrencyRate, RoundingMode.FLOOR);
    }
}
