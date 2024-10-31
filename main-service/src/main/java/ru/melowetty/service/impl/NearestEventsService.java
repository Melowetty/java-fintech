package ru.melowetty.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.melowetty.model.Event;
import ru.melowetty.service.KudagoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class NearestEventsService {
    private final CurrencyService currencyService;
    private final KudagoService kudagoService;

    public NearestEventsService(CurrencyService currencyService, KudagoService kudagoService) {
        this.currencyService = currencyService;
        this.kudagoService = kudagoService;
    }

    @Async("asyncExecutor")
    public CompletableFuture<List<Event>> getEventsByBudgetFuture(BigDecimal budget, Currency currency, LocalDate from, LocalDate to) {
        var completableFutureEvents = CompletableFuture.supplyAsync(() -> {
            List<Event> events = new ArrayList<>();
            List<Event> result;
            var currentPage = 1;
            do {
                log.info("Получение событий на странице {}", currentPage);
                result = kudagoService.getEvents(from, to, currentPage);
                currentPage += 1;
                events.addAll(result);
            } while (!result.isEmpty());
            return events;
        });

        var completableFutureBudget = CompletableFuture.supplyAsync(() -> currencyService.getConvertedAmount(currency));

        var result = new CompletableFuture<List<Event>>();

        completableFutureBudget.thenAcceptBoth(completableFutureEvents, (convertedBudget, events) -> {
            log.info("Бюджет – {}, количество ивентов – {}", convertedBudget, events.size());

            result.complete(events.stream()
                    .filter((event) -> !(event.price == null))
                    .filter((event) -> convertedBudget.compareTo(event.price) >= 0)
                    .toList());
        });

        return result;
    }
}
