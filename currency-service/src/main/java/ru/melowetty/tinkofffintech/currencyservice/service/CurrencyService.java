package ru.melowetty.tinkofffintech.currencyservice.service;

import ru.melowetty.tinkofffintech.currencyservice.model.CurrencyRate;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;

public interface CurrencyService {
    BigDecimal getCurrencyRate(Currency currency);

    List<CurrencyRate> getCurrenciesRate();
}
