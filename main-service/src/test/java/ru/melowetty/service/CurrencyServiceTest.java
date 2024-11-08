package ru.melowetty.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;
import ru.melowetty.service.impl.CurrencyService;

import java.math.BigDecimal;
import java.util.Currency;

@SpringBootTest
@Testcontainers
public class CurrencyServiceTest {
    @Autowired
    private CurrencyService currencyService;

    @Container
    static WireMockContainer wireMock = new WireMockContainer("wiremock/wiremock:3.2.0-alpine")
            .withMappingFromResource(CurrencyServiceTest.class, "convert.json");

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("api.currency-service.base-path", () -> wireMock.getUrl(""));
    }

    @AfterAll
    public static void tearDown() {
        wireMock.stop();
    }

    @Test
    public void testGetConvertedAmount() {
        var amount = currencyService.getConvertedAmount(Currency.getInstance("USD"), BigDecimal.ONE);
        Assertions.assertEquals(amount, BigDecimal.valueOf(50));
    }
}
