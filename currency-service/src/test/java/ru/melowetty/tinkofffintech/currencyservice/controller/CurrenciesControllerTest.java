package ru.melowetty.tinkofffintech.currencyservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.melowetty.tinkofffintech.currencyservice.exception.CentralBankServiceUnavailableException;
import ru.melowetty.tinkofffintech.currencyservice.exception.CurrencyNotFoundAtCentralBankException;
import ru.melowetty.tinkofffintech.currencyservice.service.CurrencyConverterService;
import ru.melowetty.tinkofffintech.currencyservice.service.CurrencyService;

import java.math.BigDecimal;
import java.util.Currency;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CurrenciesController.class)
public class CurrenciesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @MockBean
    private CurrencyConverterService currencyConverterService;

    @Test
    public void getCurrencyRateValidCode() throws Exception {
        Currency usd = Currency.getInstance("USD");
        BigDecimal rate = new BigDecimal("10.5");

        when(currencyService.getCurrencyRate(eq(usd))).thenReturn(rate);

        mockMvc.perform(get("/currencies/rates/USD"))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"currency\":\"USD\",\"rate\":10.5}"));
    }

    @Test
    public void getCurrencyRateInvalidCode() throws Exception {
        mockMvc.perform(get("/currencies/rates/INVALID"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getCurrencyWhichNotExistsInCentralBank() throws Exception {
        Currency usd = Currency.getInstance("USD");

        when(currencyService.getCurrencyRate(eq(usd))).thenThrow(new CurrencyNotFoundAtCentralBankException(usd));

        mockMvc.perform(get("/currencies/rates/USD"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getCurrencyButCentralBankIsDowned() throws Exception {
        when(currencyService.getCurrencyRate(any())).thenThrow(new CentralBankServiceUnavailableException());

        mockMvc.perform(get("/currencies/rates/USD"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(header().string("Retry-After", "3600"));
    }

    @Test
    public void testConvertCurrency() throws Exception {
        Currency usd = Currency.getInstance("USD");
        Currency rub = Currency.getInstance("RUB");
        BigDecimal amount = new BigDecimal("100.50");
        BigDecimal convertedAmount = new BigDecimal("10.1");

        when(currencyConverterService.convertCurrency(eq(usd), eq(rub), eq(amount))).thenReturn(convertedAmount);

        String requestJson = "{\"fromCurrency\":\"USD\",\"toCurrency\":\"RUB\",\"amount\":100.50}";

        mockMvc.perform(post("/currencies/convert")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"fromCurrency\":\"USD\",\"toCurrency\":\"RUB\",\"convertedAmount\":10.1}"));
    }

    @Test
    public void testValidateNegativeAmount() throws Exception {
        String negativeAmountJson = "{\"fromCurrency\":\"USD\",\"toCurrency\":\"RUB\",\"amount\":-100.50}";
        mockMvc.perform(post("/currencies/convert")
                        .contentType("application/json")
                        .content(negativeAmountJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testValidateNonExistsRequiredField() throws Exception {
        String nonExistsRequiredField = "{\"fromCurrency\":\"USD\",\"toCurrency\":\"RUB\"}";
        mockMvc.perform(post("/currencies/convert")
                        .contentType("application/json")
                        .content(nonExistsRequiredField))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testValidateInvalidCurrencyCode() throws Exception {
        String invalidCurrencyCode = "{\"fromCurrency\":\"...\",\"toCurrency\":\"RUB\",\"amount\":100.50}";
        mockMvc.perform(post("/currencies/convert")
                        .contentType("application/json")
                        .content(invalidCurrencyCode))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testValidateInvalidLengthCurrencyCode() throws Exception {
        String invalidCurrencyCodeLengthJson = "{\"fromCurrency\":\"US\",\"toCurrency\":\"RUB\",\"amount\":100.50}";
        mockMvc.perform(post("/currencies/convert")
                        .contentType("application/json")
                        .content(invalidCurrencyCodeLengthJson))
                .andExpect(status().isBadRequest());
    }
}