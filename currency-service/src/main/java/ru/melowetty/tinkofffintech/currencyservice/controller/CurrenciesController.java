package ru.melowetty.tinkofffintech.currencyservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.melowetty.tinkofffintech.currencyservice.controller.request.CurrencyConvertRequest;
import ru.melowetty.tinkofffintech.currencyservice.controller.response.CurrencyConvertResponse;
import ru.melowetty.tinkofffintech.currencyservice.controller.response.CurrencyRateResponse;
import ru.melowetty.tinkofffintech.currencyservice.service.CurrencyConverterService;
import ru.melowetty.tinkofffintech.currencyservice.service.CurrencyService;
import ru.melowetty.tinkofffintech.currencyservice.util.CurrencyUtils;

import java.math.BigDecimal;

@RestController
@Tag(name = "Сервис конвертации валют")
@RequestMapping("/currencies")
public class CurrenciesController {
    private final CurrencyService currencyService;
    private final CurrencyConverterService currencyConverterService;

    public CurrenciesController(CurrencyService currencyService, CurrencyConverterService currencyConverterService) {
        this.currencyService = currencyService;
        this.currencyConverterService = currencyConverterService;
    }

    @GetMapping("/rates/{code}")
    @Operation(summary = "Получение курса валюты по её коду")
    public CurrencyRateResponse getCurrencyRate(@PathVariable @Parameter(name = "Код валюты") String code) {
        var currency = CurrencyUtils.getCurrency(code);
        if (currency == null)
            throw new IllegalArgumentException("Такой валюты не существует");

        BigDecimal rate = currencyService.getCurrencyRate(currency);

        return new CurrencyRateResponse(currency, rate);
    }

    @PostMapping("/convert")
    @Operation(summary = "Конвертировать одну валюту в другую")
    public CurrencyConvertResponse convertCurrency(@Valid @RequestBody CurrencyConvertRequest request) {
        var fromCurrency = CurrencyUtils.getCurrency(request.fromCurrency);
        var toCurrency = CurrencyUtils.getCurrency(request.toCurrency);
        BigDecimal amount = currencyConverterService.convertCurrency(fromCurrency, toCurrency, request.amount);
        return new CurrencyConvertResponse(fromCurrency, toCurrency, amount);
    }
}
