package ru.melowetty.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import ru.melowetty.model.EventDto;
import ru.melowetty.service.impl.NearestEventsService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("events")
@Valid
public class EventController {
    private final NearestEventsService eventsService;

    public EventController(NearestEventsService eventsService) {
        this.eventsService = eventsService;
    }

    @GetMapping("/future")
    public CompletableFuture<List<EventDto>> completableFutureGetEventsByBudget(
            @NotNull(message = "Не указан бюджет")
                    @RequestParam("budget")
                    @DecimalMin(value = "0.0", message = "Бюджет не должен быть меньше нуля!")
            BigDecimal budget,

            @RequestParam("currency")
            String currencyRaw,

            @RequestParam(value = "dateFrom", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateFrom,

            @RequestParam(value = "dateTo", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateTo
    ) {
        Currency currency = Currency.getInstance(currencyRaw);

        if (dateFrom == null || dateTo == null) {
            dateFrom = LocalDate.now();
            dateTo = LocalDate.now().plusDays(7);
        }

        return eventsService.getEventsByBudgetFuture(budget, currency, dateFrom, dateTo);
    }

    @GetMapping("/reactor")
    public Flux<EventDto> reactorGetEventsByBudget(
            @NotNull(message = "Не указан бюджет")
            @RequestParam("budget")
            @DecimalMin(value = "0.0", message = "Бюджет не должен быть меньше нуля!")
            BigDecimal budget,

            @RequestParam("currency")
            String currencyRaw,

            @RequestParam(value = "dateFrom", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateFrom,

            @RequestParam(value = "dateTo", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateTo
    ) {
        Currency currency = Currency.getInstance(currencyRaw);

        if (dateFrom == null || dateTo == null) {
            dateFrom = LocalDate.now();
            dateTo = LocalDate.now().plusDays(7);
        }

        return eventsService.getEventsByBudgetReactor(budget, currency, dateFrom, dateTo);
    }
}
