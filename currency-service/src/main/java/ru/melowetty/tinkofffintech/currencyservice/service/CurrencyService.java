package ru.melowetty.tinkofffintech.currencyservice.service;

import java.util.Currency;

public interface CurrencyService {
    float getCurrencyRate(Currency currency);

    float convertCurrency(Currency fromCurrency, Currency toCurrency, float initialAmount);
}
