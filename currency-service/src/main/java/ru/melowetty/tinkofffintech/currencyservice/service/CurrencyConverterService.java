package ru.melowetty.tinkofffintech.currencyservice.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Currency;

@Service
public class CurrencyConverterService {
    public BigDecimal convertCurrency(Currency fromCurrency, Currency toCurrency, BigDecimal initialAmount) {
        return BigDecimal.ZERO;
    }
}
