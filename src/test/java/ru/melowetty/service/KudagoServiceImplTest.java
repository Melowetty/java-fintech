package ru.melowetty.service;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.wiremock.integrations.testcontainers.WireMockContainer;
import ru.melowetty.model.Category;
import ru.melowetty.model.Location;
import ru.melowetty.service.impl.KudagoServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Testcontainers
public class KudagoServiceImplTest {
    @Container
    static WireMockContainer wireMock = new WireMockContainer("wiremock/wiremock:3.2.0-alpine")
            .withMappingFromResource(KudagoServiceImplTest.class, "locations.json")
            .withMappingFromResource(KudagoServiceImplTest.class, "categories.json");
    @Autowired
    private KudagoServiceImpl kudagoService;

    @DynamicPropertySource
    public static void properties(DynamicPropertyRegistry registry) {
        registry.add("api.kudago.base-path", () -> wireMock.getUrl(""));
    }

    @AfterAll
    public static void tearDown() {
        wireMock.stop();
    }

    @Test
    public void getLocations_returnsLocations() {
        var actual = kudagoService.getLocations();
        var expected = List.of(new Location("spb", "Saint Petersburg"), new Location("moscow", "Moscow"));

        assertEquals(expected, actual);
    }

    @Test
    public void getCategories_returnsCategories() {
        var actual = kudagoService.getCategories();
        var expected = List.of(new Category(0, "cafe", "Кафе"), new Category(0, "house", "Дом"));

        assertEquals(expected, actual);
    }
}