package ru.melowetty.tinkofffintech.currencyservice.service.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.melowetty.tinkofffintech.currencyservice.exception.CentralBankServiceUnavailableException;
import ru.melowetty.tinkofffintech.currencyservice.exception.CurrencyNotFoundAtCentralBankException;
import ru.melowetty.tinkofffintech.currencyservice.model.CurrencyRate;
import ru.melowetty.tinkofffintech.currencyservice.service.CurrencyService;
import ru.melowetty.tinkofffintech.currencyservice.util.CurrencyUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

@Service
public class CentralBankCurrencyService implements CurrencyService {
    private static final String serviceName = "central-bank-service";
    private final RestTemplate restTemplate;
    @Value("${api.central-bank.url}")
    public String baseUrl;

    public CentralBankCurrencyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @Cacheable(value = "currency-rate")
    @CircuitBreaker(name = serviceName, fallbackMethod = "fallbackCurrencyRate")
    public BigDecimal getCurrencyRate(Currency currency) {
        CurrencyRate rate = getCurrenciesRate().stream()
                .filter((currencyRate) -> currencyRate.currency.equals(currency))
                .findFirst().orElseThrow(() -> new CurrencyNotFoundAtCentralBankException(currency));

        return rate.rate;
    }

    public BigDecimal fallbackCurrencyRate(Currency currency, Exception e) {
        if (e instanceof CurrencyNotFoundAtCentralBankException) {
            throw (CurrencyNotFoundAtCentralBankException) e;
        }
        throw new CentralBankServiceUnavailableException();
    }

    public List<CurrencyRate> fallbackCurrencyRates(Exception e) {
        throw new CentralBankServiceUnavailableException();
    }

    @Override
    @Cacheable(value = "currency-rates")
    @CircuitBreaker(name = serviceName, fallbackMethod = "fallbackCurrencyRates")
    public List<CurrencyRate> getCurrenciesRate() {
        var params = new HashMap<String, String>();

        var currentDate = LocalDate.now(ZoneOffset.ofHours(2).normalized());
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        params.put("data_req", formatter.format(currentDate));

        var response = restTemplate.getForEntity(baseUrl + "/scripts/XML_daily.asp", CentralBankCurrencyRate[].class, params);

        if (response.getStatusCode().is5xxServerError()) {
            throw new CentralBankServiceUnavailableException();
        }

        if (response.getBody() == null) {
            throw new RuntimeException("Произошла ошибка во время обработки ответа от центробанка");
        }

        return Arrays.stream(response.getBody())
                .filter((rate) -> rate.CharCode != null && rate.VunitRate != null)
                .map((rate) ->
                        new CurrencyRate(CurrencyUtils.getCurrency(rate.CharCode), new BigDecimal(rate.VunitRate.replace(",", ".")))
                )
                .toList();
    }

    @Data
    @NoArgsConstructor
    static class CentralBankCurrencyRate {
        public String CharCode;

        public String VunitRate;
    }
}
