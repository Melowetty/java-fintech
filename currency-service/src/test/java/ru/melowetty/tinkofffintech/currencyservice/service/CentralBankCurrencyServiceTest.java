package ru.melowetty.tinkofffintech.currencyservice.service;

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;
import ru.melowetty.tinkofffintech.currencyservice.exception.CentralBankServiceUnavailableException;
import ru.melowetty.tinkofffintech.currencyservice.exception.CurrencyNotFoundAtCentralBankException;
import ru.melowetty.tinkofffintech.currencyservice.service.impl.CentralBankCurrencyService;

import java.math.BigDecimal;
import java.util.Currency;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.notFound;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.serviceUnavailable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

@Testcontainers
@SpringBootTest
public class CentralBankCurrencyServiceTest {
    @Container
    static WireMockContainer wireMock = new WireMockContainer("wiremock/wiremock:3.2.0-alpine");

    @Autowired
    private CentralBankCurrencyService currencyService;

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("api.central-bank.url", () -> wireMock.getBaseUrl());
    }

    @AfterAll
    public static void tearDown() {
        wireMock.stop();
    }

//    @Test
//    public void testGetCurrenciesRate_butCentralBankIsDowned() {
//        new WireMock(wireMock.getHost(), wireMock.getPort())
//                .register(
//                        WireMock.get(WireMock.urlPathEqualTo("/scripts/XML_daily.asp"))
//                                .willReturn(serviceUnavailable())
//                );
//
//        assertThrows(CentralBankServiceUnavailableException.class, () -> currencyService.getCurrenciesRate());
//    }


    @Test
    public void testGetCurrenciesRate_returnsCurrenciesRate() {
        new WireMock(wireMock.getHost(), wireMock.getPort())
                .register(
                        WireMock.get(WireMock.urlPathEqualTo("/scripts/XML_daily.asp"))
                                .willReturn(ok().withHeader("content-type", "application/xml").withBody(readXmlResponse()))
                );
        var rates = currencyService.getCurrenciesRate();

        Assertions.assertEquals(1, rates.size());

        var rate = rates.get(0);

        Assertions.assertEquals("AUD", rate.getCurrency().getCurrencyCode());
    }

    @Test
    public void testGetCurrencyRate_returnCurrencyRate() {
        new WireMock(wireMock.getHost(), wireMock.getPort())
                .register(
                        WireMock.get(WireMock.urlPathEqualTo("/scripts/XML_daily.asp"))
                                .willReturn(ok().withHeader("content-type", "application/xml").withBody(readXmlResponse()))
                );

        var actual = currencyService.getCurrencyRate(Currency.getInstance("AUD"));
        var expected = BigDecimal.valueOf(50.5);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void testGetNonExistCurrencyRate_returnCurrencyNotFound() {
        new WireMock(wireMock.getHost(), wireMock.getPort())
                .register(
                        WireMock.get(WireMock.urlPathEqualTo("/scripts/XML_daily.asp"))
                                .willReturn(ok().withHeader("content-type", "application/xml").withBody(readXmlResponse()))
                );

        assertThrows(CurrencyNotFoundAtCentralBankException.class,
                () -> currencyService.getCurrencyRate(Currency.getInstance("USD")));
    }

    private String readXmlResponse() {
        return """
                <?xml version="1.0" encoding="windows-1251"?>
                <ValCurs Date="01.01.2021" name="Foreign Currency Market">
                    <Valute ID="R01010">
                        <NumCode>036</NumCode>
                        <CharCode>AUD</CharCode>
                        <Nominal>1</Nominal>
                        <Name>Australian Dollar</Name>
                        <Value>50,5</Value>
                        <VunitRate>50,5</VunitRate>
                    </Valute>
                </ValCurs>
                """;
    }
}
