package ru.melowetty.tinkofffintech.currencyservice.util;

import java.util.Currency;
import java.util.Locale;

public class CurrencyUtils {
    public static Currency getCurrency(String targetCurrency) {
        try {
            return Currency.getInstance(targetCurrency.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
