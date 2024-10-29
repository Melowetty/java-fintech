package ru.melowetty.tinkofffintech.currencyservice.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Currency;

@ExtendWith(MockitoExtension.class)
public class CurrencyConverterServiceTest {
    @InjectMocks
    CurrencyConverterService currencyConverterService;

    @Mock
    CurrencyService currencyService;

    @Test
    public void test_currency_converting() {
        var fromCurrency = Currency.getInstance("USD");
        var toCurrency = Currency.getInstance("EUR");
        var amount = BigDecimal.valueOf(10);

        Mockito.when(currencyService.getCurrencyRate(fromCurrency)).thenReturn(BigDecimal.valueOf(80));
        Mockito.when(currencyService.getCurrencyRate(toCurrency)).thenReturn(BigDecimal.valueOf(100));

        BigDecimal actual = currencyConverterService.convertCurrency(fromCurrency, toCurrency, amount);
        BigDecimal expected = BigDecimal.valueOf(8);

        Assertions.assertEquals(expected, actual);

    }

    @Test
    public void convertingToNonRubCurrency() {
        var fromCurrency = Currency.getInstance("RUB");
        var toCurrency = Currency.getInstance("USD");
        var amount = BigDecimal.valueOf(100);

        Mockito.when(currencyService.getCurrencyRate(toCurrency)).thenReturn(BigDecimal.valueOf(80));

        BigDecimal actual = currencyConverterService.convertCurrency(fromCurrency, toCurrency, amount);
        BigDecimal expected = BigDecimal.valueOf(1.25);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void convertingFromAndToRubCurrency() {
        var fromCurrency = Currency.getInstance("RUB");
        var toCurrency = Currency.getInstance("RUB");
        var amount = BigDecimal.valueOf(100);

        BigDecimal actual = currencyConverterService.convertCurrency(fromCurrency, toCurrency, amount);
        BigDecimal expected = BigDecimal.valueOf(100);

        Assertions.assertEquals(expected, actual);
    }
}
