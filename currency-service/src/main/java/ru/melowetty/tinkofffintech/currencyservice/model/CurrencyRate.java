package ru.melowetty.tinkofffintech.currencyservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Currency;

@Data
@AllArgsConstructor
public class CurrencyRate {
    public Currency currency;
    public BigDecimal rate;
}
