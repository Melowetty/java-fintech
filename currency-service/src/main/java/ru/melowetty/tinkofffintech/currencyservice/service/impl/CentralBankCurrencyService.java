package ru.melowetty.tinkofffintech.currencyservice.service.impl;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.HashMap;
import java.util.List;

@Service
public class CentralBankCurrencyService implements CurrencyService {
    private final RestTemplate restTemplate;

    @Value("api.central-bank.url")
    private String baseUrl;

    public CentralBankCurrencyService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @Cacheable("currency-rate")
    public BigDecimal getCurrencyRate(Currency currency) {
        CurrencyRate rate = getCurrenciesRate().stream()
                .filter((currencyRate) -> currencyRate.currency.equals(currency))
                .findFirst().orElseThrow(() -> new CurrencyNotFoundAtCentralBankException(currency));

        return rate.rate;
    }

    @Override
    @Cacheable("currency-rates")
    public List<CurrencyRate> getCurrenciesRate() {
        var params = new HashMap<String, String>();

        var currentDate = LocalDate.now(ZoneOffset.ofHours(2).normalized());
        var formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        params.put("data_req", formatter.format(currentDate));

        var response = restTemplate.getForEntity(baseUrl + "/scripts/XML_daily.asp", CentralBankCurrenciesRateResponse.class, params);
        if (response.getStatusCode().value() == 503) {
            throw new CentralBankServiceUnavailableException();
        }

        if (response.getBody() == null) {
            throw new RuntimeException("Произошла ошибка во время парсинга ответа от центробанка");
        }

        return response.getBody().currencyRates
                .stream()
                .map((rate) -> new CurrencyRate(Currency.getInstance(rate.charCode), rate.unitRate))
                .toList();
    }

    @Data
    @NoArgsConstructor
    static class CentralBankCurrenciesRateResponse {
        @XmlElement(name = "ValCurs")
        public List<CentralBankCurrencyRate> currencyRates;
    }

    @Data
    @NoArgsConstructor
    @XmlRootElement(name = "Valute")
    static class CentralBankCurrencyRate {
        @XmlElement(name = "CharCode")
        public String charCode;

        @XmlElement(name = "VunitRate")
        public BigDecimal unitRate;
    }
}
