package ru.melowetty.service.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.melowetty.model.Category;
import ru.melowetty.model.EventDto;
import ru.melowetty.model.Location;
import ru.melowetty.service.KudagoService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class KudagoServiceImpl implements KudagoService {
    private final RestTemplate restTemplate;
    private final Semaphore semaphore;
    private final RetryTemplate retryTemplate;

    @Value("${api.kudago.base-path}")
    private String BASE_URL;

    public KudagoServiceImpl(
            RestTemplate restTemplate,
            @Qualifier("kudago_semaphore")
                             Semaphore semaphore,
            RetryTemplate retryTemplate) {
        this.restTemplate = restTemplate;
        this.semaphore = semaphore;
        this.retryTemplate = retryTemplate;
    }

    @Override
    public List<Category> getCategories() {
        try {
            semaphore.acquire();

            var response = retryTemplate.execute(context -> restTemplate.getForObject(BASE_URL + "/place-categories/",
                    KudagoCategoryResponse[].class));

            if (response == null) {
                throw new RestClientException("Ответ от Kudago API пустой");
            }

            return Arrays.stream(response).map(c -> {
                var category = new Category();
                category.setName(c.name);
                category.setSlug(c.slug);

                return category;
            }).toList();
        } catch (RestClientException | InterruptedException e) {
            log.error("Произошла ошибка во время получения данных о категориях из Kudago API", e);
        } finally {
            semaphore.release();
        }
        return List.of();
    }

    @Override
    public List<Location> getLocations() {
        try {
            semaphore.acquire();

            var response = retryTemplate.execute(context ->
                    restTemplate.getForObject(BASE_URL + "/locations/", KudagoLocationResponse[].class));

            if (response == null) {
                throw new RestClientException("Ответ от Kudago API пустой");
            }

            return Arrays.stream(response).map(l -> {
                var location = new Location();
                location.setName(l.name);
                location.setSlug(l.slug);

                return location;
            }).toList();
        } catch (RestClientException | InterruptedException e) {
            log.error("Произошла ошибка во время получения данных о городах из Kudago API", e);
        } finally {
            semaphore.release();
        }
        return List.of();
    }

    @Override
    public List<EventDto> getEvents(LocalDate dateFrom, LocalDate dateTo, int page) {
        try {
            semaphore.acquire();
            var variables = new HashMap<String, String>();

            String urlTemplate = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/events")
                    .queryParam("actual_since", dateFrom.atStartOfDay().toInstant(ZoneOffset.ofHours(2)).getEpochSecond())
                    .queryParam("actual_until", dateTo.atStartOfDay().toInstant(ZoneOffset.ofHours(2)).getEpochSecond())
                    .queryParam("page", page)
                    .queryParam("page_size", 100)
                    .queryParam("text_format", "text")
                    .queryParam("fields", "id,title,price,is_free,dates")
                    .encode()
                    .toUriString();

            var response = retryTemplate.execute(context ->
                    restTemplate.getForObject(urlTemplate, PagedKudagoEventsResponse.class, variables));

            if (response == null) {
                throw new RestClientException("Ответ от Kudago API пустой");
            }

            return response.results.stream().map(event -> {
                var dates = event.dates.stream().map((date) -> new EventDto.EventDates(date.start, date.end))
                        .toList();
                return EventDto.builder()
                        .id(event.id)
                        .title(event.title)
                        .price(getPriceOfEvent(event))
                        .dates(dates)
                        .build();
            }).toList();


        } catch (RuntimeException | InterruptedException e) {
            log.error("Произошла ошибка во время получения данных об событиях из Kudago API", e);
        } finally {
            semaphore.release();
        }

        return List.of();
    }

    private BigDecimal getPriceOfEvent(KudagoEvent event) {
        if (event == null || event.price.isEmpty()) return null;
        if (event.isFree) return BigDecimal.ZERO;
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(event.price);

        BigDecimal lastNumber = null;

        while (matcher.find()) {
            lastNumber = new BigDecimal(matcher.group());
        }

        return lastNumber;
    }

    @Data
    static class KudagoLocationResponse {
        public String slug;
        public String name;
    }

    @Data
    static class KudagoCategoryResponse {
        public int id;
        public String slug;
        public String name;
    }

    @Data
    static class KudagoEvent {
        public int id;

        public String title;

        public String price;

        public boolean isFree;

        @JsonProperty("dates")
        public List<KudagoDate> dates;
    }

    @Data
    static class KudagoDate {
        public Instant start;
        public Instant end;
    }

    @Data
    static class PagedKudagoEventsResponse {
        private final List<KudagoEvent> results = new ArrayList<>();
        private int count;
        private String next;
        private String previous;
    }
}
