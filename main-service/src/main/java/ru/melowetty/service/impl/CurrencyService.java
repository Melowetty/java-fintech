package ru.melowetty.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Objects;

@Service
@Slf4j
public class CurrencyService {
    @Value("${api.currency-service.base-path}")
    private String BASE_PATH;
    private final RetryTemplate retryTemplate;
    private final RestTemplate restTemplate;

    public CurrencyService(RetryTemplate retryTemplate, RestTemplate restTemplate) {
        this.retryTemplate = retryTemplate;
        this.restTemplate = restTemplate;
    }

    public BigDecimal getConvertedAmount(Currency currency, BigDecimal budget) {
        if (Objects.equals(budget, BigDecimal.ZERO)) return BigDecimal.ZERO;
        try {
            var url = BASE_PATH + "/currencies/convert";
            var request = new HashMap<String, Object>();
            request.put("fromCurrency", currency.getCurrencyCode());
            request.put("toCurrency", "RUB");
            request.put("amount", budget.toPlainString());

            var response = retryTemplate.execute(context ->
                    restTemplate.postForEntity(url, request, CurrencyConvertResponse.class));

            if (response.getStatusCode().is2xxSuccessful() && response.hasBody()) {
                return response.getBody().convertedAmount;
            }

            if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new RuntimeException("Такой валюты нет в ЦБ");
            }

            else {
                throw new RuntimeException("Произошла неопределенная ошибка во время обработки данных от сервиса курса валют");
            }
        } catch (RuntimeException e) {
            log.error("Произошла ошибка во время получения курса валюты", e);
            throw e;
        }
    }

    private static class CurrencyConvertResponse {
        public Currency fromCurrency;
        public Currency toCurrency;
        public BigDecimal convertedAmount;
    }

}
