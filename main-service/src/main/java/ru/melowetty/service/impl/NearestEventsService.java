package ru.melowetty.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.melowetty.model.EventDto;
import ru.melowetty.service.KudagoService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class NearestEventsService {
    private final CurrencyService currencyService;
    private final KudagoService kudagoService;
    private final int MAX_PAGE_COUNT = 5;

    public NearestEventsService(CurrencyService currencyService, KudagoService kudagoService) {
        this.currencyService = currencyService;
        this.kudagoService = kudagoService;
    }

    public CompletableFuture<List<EventDto>> getEventsByBudgetFuture(BigDecimal budget, Currency currency, LocalDate from, LocalDate to) {
        var completableFutureEvents = CompletableFuture.supplyAsync(() -> {
            List<EventDto> events = new ArrayList<>();
            List<EventDto> result;
            var currentPage = 1;
            do {
                log.info("Получение событий на странице {}", currentPage);
                result = kudagoService.getEvents(from, to, currentPage);
                currentPage += 1;
                if (currentPage == MAX_PAGE_COUNT) break;
                events.addAll(result);
            } while (!result.isEmpty());
            return events;
        });

        var completableFutureBudget = CompletableFuture.supplyAsync(() ->  {
            log.info("Получение сконвертированного бюджета Future");
            return currencyService.getConvertedAmount(currency, budget);

        });

        var result = new CompletableFuture<List<EventDto>>();

        completableFutureBudget.thenAcceptBoth(completableFutureEvents, (convertedBudget, events) -> {
            log.info("Бюджет – {}, количество ивентов – {}", convertedBudget, events.size());

            result.complete(events.stream()
                    .filter((event) -> !(event.price == null))
                    .filter((event) -> convertedBudget.compareTo(event.price) >= 0)
                    .toList());
        }).exceptionally((e) -> {
            log.error("Произошла ошибка во время получения данных", e);
                    result.completeExceptionally(e);
                    return null;
                }
        );

        return result;
    }

    public Flux<EventDto> getEventsByBudgetReactor(BigDecimal budget, Currency currency, LocalDate from, LocalDate to) {
        var currentPage = new AtomicInteger(1);
        Flux<EventDto> fluxEvents = Flux.create(emitter -> {
            List<EventDto> result;
            do {
                log.info("Получение событий на странице {}", currentPage.get());
                result = kudagoService.getEvents(from, to, currentPage.get());
                currentPage.getAndIncrement();

                result.forEach((emitter::next));
                
                if (currentPage.get() >= MAX_PAGE_COUNT) break;
            } while (!result.isEmpty());
            emitter.complete();
        });

        var monoConvertedAmount = Mono.fromCallable(() -> {
            log.info("Получение сконвертированного бюджета");
            return currencyService.getConvertedAmount(currency, budget);
        });

        return Flux.zip(fluxEvents, monoConvertedAmount)
                .flatMap(tuple -> {
                    var convertedBudget = tuple.getT2();

                    log.info("Началась обработка данных");
                    return fluxEvents
                            .filter((event) -> !(event.price == null))
                            .filter((event) -> convertedBudget.compareTo(event.price) >= 0);
                })
                .onErrorResume(e -> {
                    log.error("Возникла ошибка при считывании данных об ивентах", e);
                    return Mono.error(e);
                });
    }
}
