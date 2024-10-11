package ru.melowetty.tinkofffintech.currencyservice.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

public class RegexTest {
    @Test
    public void testRegexDate() {
        var predicate = Pattern.compile("[0-9]{2}/[0-9]{2}/[0-9]{4}").asPredicate();

        Assertions.assertTrue(predicate.test("10/10/2010"));
    }
}
