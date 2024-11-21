package ru.melowetty.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.melowetty.service.EventService;
import ru.melowetty.service.PlaceService;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(
        properties = {
                "spring.datasource.url=jdbc:tc:postgresql:16-alpine:///db"
        }
)
@Testcontainers
@SpringBootTest
@ActiveProfiles("test-with-db")
@AutoConfigureMockMvc(addFilters = false)
public class PlaceControllerE2ETest {
    @Autowired
    private EventService eventService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PlaceService placeService;

    @BeforeEach
    public void clearData() {
        eventService.deleteAll();
        placeService.deleteAll();
    }

    @Test
    @DisplayName("Test create place")
    public void testCreatePlace() throws Exception {
        var request = "{\"name\":\"Test\",\"slug\":\"test\"}";

        mockMvc.perform(post("/place")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.slug").value("test"));

        Assertions.assertEquals(1, (long) placeService.count());
    }

    @Test
    @DisplayName("Test update place name and slug")
    public void testUpdatePlace() throws Exception {
        var place = placeService.createPlace("test", "test2");

        var request = "{\"name\":\"new-Test\",\"slug\":\"new-test\"}";

        mockMvc.perform(put("/place/" + place.getId())
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(place.getId()))
                .andExpect(jsonPath("$.name").value("new-Test"))
                .andExpect(jsonPath("$.slug").value("new-test"));

        var placeFromDb = placeService.getPlaceById(place.getId());

        Assertions.assertEquals(placeFromDb.getName(), "new-Test");
        Assertions.assertEquals(placeFromDb.getSlug(), "new-test");
    }

    @Test
    @DisplayName("Test update place but place is not exist")
    public void testUpdatePlaceButPlaceIsNotExist() throws Exception {
        var request = "{\"name\":\"new-Test\",\"slug\":\"new-test\"}";

        mockMvc.perform(put("/place/1")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test get place")
    public void testGetPlace() throws Exception {
        var place = placeService.createPlace("test", "test2");

        var event = eventService.createEvent("test", LocalDateTime.now(), place.getId());

        mockMvc.perform(get("/place/" + place.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(place.getId()))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.slug").value("test2"))
                .andExpect(jsonPath("$.events", hasSize(1)));
    }

    @Test
    @DisplayName("Test get place without events")
    public void testGetPlaceWithoutEvents() throws Exception {
        var place = placeService.createPlace("test", "test2");

        mockMvc.perform(get("/place/" + place.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(place.getId()))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.slug").value("test2"))
                .andExpect(jsonPath("$.events", hasSize(0)));
    }

    @Test
    @DisplayName("Test get place when it is not exist")
    public void testGetEventWhenNotExist() throws Exception {
        mockMvc.perform(get("/place/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test delete place")
    public void testDeleteEvent() throws Exception {
        var place = placeService.createPlace("test", "test2");

        mockMvc.perform(delete("/place/" + place.getId()))
                .andExpect(status().isOk());

        Assertions.assertEquals(0, (long) placeService.count());
    }

    @Test
    @DisplayName("Test delete event when it not exist")
    public void testDeleteEventWhenItNotExist() throws Exception {
        mockMvc.perform(delete("/place/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test get all events")
    public void testGetAllEvents() throws Exception {
        var place = placeService.createPlace("test", "test2");

        var place2 = placeService.createPlace("test2", "test3");

        Assertions.assertEquals(2L, placeService.count());

        mockMvc.perform(get("/place"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(place.getId()))
                .andExpect(jsonPath("$[0].name").value("test"))
                .andExpect(jsonPath("$[0].slug").value("test2"))
                .andExpect(jsonPath("$[1].id").value(place2.getId()))
                .andExpect(jsonPath("$[1].name").value("test2"))
                .andExpect(jsonPath("$[1].slug").value("test3"));
    }
}
