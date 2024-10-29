package ru.melowetty.tinkofffintech.currencyservice.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.melowetty.tinkofffintech.currencyservice.util.CurrencyUtils;

import java.util.Currency;

public class CurrencyUtilsTest {

    @Test
    public void test_get_currency__currency_is_exist() {
        var currency = "USD";
        var expected = Currency.getInstance(currency);

        var actual = CurrencyUtils.getCurrency("USD");

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void test_get_currency__currency_is_not_exist() {
        var actual = CurrencyUtils.getCurrency("NON_EXISTED_CURRENCY");

        Assertions.assertNull(actual);
    }

    @Test
    public void test_get_currency__currency_is_in_lower_case() {
        var expected = CurrencyUtils.getCurrency("USD");

        var actual = CurrencyUtils.getCurrency("usd");

        Assertions.assertEquals(expected, actual);
    }
}
