package ru.melowetty.tinkofffintech.currencyservice.service;

import java.math.BigDecimal;
import java.util.Currency;

public interface CurrencyService {
    BigDecimal getCurrencyRate(Currency currency);

    BigDecimal convertCurrency(Currency fromCurrency, Currency toCurrency, BigDecimal initialAmount);
}
