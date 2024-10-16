package ru.melowetty.service.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.melowetty.model.Category;
import ru.melowetty.model.Location;
import ru.melowetty.service.KudagoService;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class KudagoServiceImpl implements KudagoService {
    private final RestTemplate restTemplate;
    @Value("${api.kudago.base-path}")
    private String BASE_URL;

    public KudagoServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<Category> getCategories() {
        try {
            var response = restTemplate.getForObject(BASE_URL + "/place-categories/",
                    KudagoCategoryResponse[].class);

            if (response == null) {
                throw new RestClientException("Ответ от Kudago API пустой");
            }

            return Arrays.stream(response).map(c -> {
                var category = new Category();
                category.setName(c.name);
                category.setSlug(c.slug);

                return category;
            }).toList();
        } catch (RestClientException e) {
            log.error("Произошла ошибка во время получения данных о категориях из Kudago API", e);
        }
        return List.of();
    }

    @Override
    public List<Location> getLocations() {
        try {
            var response = restTemplate.getForObject(BASE_URL + "/locations/", KudagoLocationResponse[].class);

            if (response == null) {
                throw new RestClientException("Ответ от Kudago API пустой");
            }

            return Arrays.stream(response).map(l -> {
                var location = new Location();
                location.setName(l.name);
                location.setSlug(l.slug);

                return location;
            }).toList();
        } catch (RestClientException e) {
            log.error("Произошла ошибка во время получения данных о городах из Kudago API", e);
        }
        return List.of();
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
}
