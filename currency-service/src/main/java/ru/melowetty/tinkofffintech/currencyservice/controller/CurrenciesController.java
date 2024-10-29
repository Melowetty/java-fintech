package ru.melowetty.tinkofffintech.currencyservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.melowetty.tinkofffintech.currencyservice.controller.request.CurrencyConvertRequest;
import ru.melowetty.tinkofffintech.currencyservice.controller.response.CurrencyConvertResponse;
import ru.melowetty.tinkofffintech.currencyservice.controller.response.CurrencyRateResponse;
import ru.melowetty.tinkofffintech.currencyservice.model.BigErrorMessageResponse;
import ru.melowetty.tinkofffintech.currencyservice.model.ErrorMessageResponse;
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

    @GetMapping(value = "/rates/{code}", produces = "application/json")
    @Operation(
            summary = "Получение курса валюты по её коду",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение курса валюты", content = @Content(schema = @Schema(implementation = CurrencyRateResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Неверный код валюты", content = @Content(schema = @Schema(implementation = ErrorMessageResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Валюта не найдена в ЦБ", content = @Content(schema = @Schema(implementation = ErrorMessageResponse.class))),
                    @ApiResponse(responseCode = "503", description = "Сервис ЦБ недоступен", content = @Content(schema = @Schema(implementation = ErrorMessageResponse.class)))
            })
    public CurrencyRateResponse getCurrencyRate(@PathVariable @Parameter(name = "code", required = true, description = "Код валюты", example = "USD") String code) {
        var currency = CurrencyUtils.getCurrency(code);
        if (currency == null) {
            throw new IllegalArgumentException("Такой валюты не существует");
        }

        BigDecimal rate = currencyService.getCurrencyRate(currency);

        return new CurrencyRateResponse(currency, rate);
    }

    @PostMapping(value = "/convert", produces = "application/json", consumes = "application/json")
    @Operation(
            summary = "Конвертировать одну валюту в другую",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение конвертированной суммы", content = @Content(schema = @Schema(implementation = CurrencyConvertResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Неверный запрос", content = @Content(schema = @Schema(implementation = BigErrorMessageResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Валюта не найдена в ЦБ", content = @Content(schema = @Schema(implementation = ErrorMessageResponse.class))),
                    @ApiResponse(responseCode = "503", description = "Сервис ЦБ недоступен", content = @Content(schema = @Schema(implementation = ErrorMessageResponse.class)))
            })
    public CurrencyConvertResponse convertCurrency(@Valid @RequestBody CurrencyConvertRequest request) {
        var fromCurrency = CurrencyUtils.getCurrency(request.fromCurrency);
        var toCurrency = CurrencyUtils.getCurrency(request.toCurrency);
        BigDecimal amount = currencyConverterService.convertCurrency(fromCurrency, toCurrency, request.amount);
        return new CurrencyConvertResponse(fromCurrency, toCurrency, amount);
    }
}
