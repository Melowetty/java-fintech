package ru.melowetty.tinkofffintech.currencyservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.melowetty.tinkofffintech.currencyservice.controller.request.CurrencyConvertRequest;
import ru.melowetty.tinkofffintech.currencyservice.controller.response.CurrencyConvertResponse;
import ru.melowetty.tinkofffintech.currencyservice.controller.response.CurrencyRateResponse;

@RestController
@Tag(name = "Сервис конвертации валют")
@RequestMapping("/currencies")
public class CurrenciesController {

    @GetMapping("/rates/{code}")
    @Operation(summary = "Получение курса валюты по её коду")
    public CurrencyRateResponse getCurrencyRate(@PathVariable @Parameter(name = "Код валюты") String code) {
        return null;
    }

    @PostMapping("/convert")
    @Operation(summary = "Конвертировать одну валюту в другую")
    public CurrencyConvertResponse convertCurrency(@RequestBody CurrencyConvertRequest request) {
        return null;
    }
}
