package ru.melowetty.tinkofffintech.currencyservice.service.impl;

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
    private final RestTemplate restTemplate;

    @Value("${api.central-bank.url}")
    public String baseUrl;

    public CentralBankCurrencyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @Cacheable(value = "currency-rate")
    public BigDecimal getCurrencyRate(Currency currency) {
        CurrencyRate rate = getCurrenciesRate().stream()
                .filter((currencyRate) -> currencyRate.currency.equals(currency))
                .findFirst().orElseThrow(() -> new CurrencyNotFoundAtCentralBankException(currency));

        return rate.rate;
    }

    @Override
    @Cacheable(value = "currency-rates")
    public List<CurrencyRate> getCurrenciesRate() {
        var params = new HashMap<String, String>();

        var currentDate = LocalDate.now(ZoneOffset.ofHours(2).normalized());
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        params.put("data_req", formatter.format(currentDate));

        var response = restTemplate.getForEntity(baseUrl + "/scripts/XML_daily.asp", CentralBankCurrencyRate[].class,params);

        if (response.getStatusCode().is5xxServerError()) {
            throw new CentralBankServiceUnavailableException();
        }

        if (response.getBody() == null) {
            throw new RuntimeException("Произошла ошибка во время парсинга ответа от центробанка");
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
