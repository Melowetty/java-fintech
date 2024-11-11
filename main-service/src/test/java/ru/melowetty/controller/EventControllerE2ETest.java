package ru.melowetty.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.melowetty.service.EventService;
import ru.melowetty.service.PlaceService;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@TestPropertySource(
        properties = {
                "spring.datasource.url=jdbc:tc:postgresql:16-alpine:///db"
        }
)
@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class EventControllerE2ETest {
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
    @DisplayName("Test create event")
    public void testCreateEvent() throws Exception {
        var place = placeService.createPlace("test", "test2");

        var request = "{\"name\":\"Test\",\"date\":\"11.11.2024 00:00:00\",\"placeId\": %d}".formatted(place.getId());

        mockMvc.perform(post("/event")
                .content(request)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Test"))
                .andExpect(jsonPath("$.date").value("11.11.2024 00:00:00"))
                .andExpect(jsonPath("$.place.id").value(place.getId()))
                .andExpect(jsonPath("$.place.name").value(place.getName()))
                .andExpect(jsonPath("$.place.slug").value(place.getSlug()));

        Assertions.assertEquals(1, (long) eventService.count());
    }

    @Test
    @DisplayName("Test create event when place is not exist")
    public void testCreateEventWhenPlaceIsNotExist() throws Exception {
        var request = "{\"name\":\"Test\",\"date\":\"11.11.2024 00:00:00\",\"placeId\": 1}";

        mockMvc.perform(post("/event")
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Assertions.assertEquals(0, (long) eventService.count());
    }

    @Test
    @DisplayName("Test update event name, date and place")
    public void testUpdateEvent() throws Exception {
        var place = placeService.createPlace("test", "test2");
        var event = eventService.createEvent("test",
                LocalDateTime.of(2024, 11, 11, 0, 0, 0),
                place.getId());

        var updatedPlace = placeService.createPlace("new-test", "new-test2");

        var request = "{\"name\":\"new-test\",\"date\":\"11.11.2024 00:00:01\",\"placeId\": %d}".formatted(updatedPlace.getId());

        mockMvc.perform(put("/event/" + event.id)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("new-test"))
                .andExpect(jsonPath("$.date").value("11.11.2024 00:00:01"))
                .andExpect(jsonPath("$.place.id").value(updatedPlace.getId()))
                .andExpect(jsonPath("$.place.name").value("new-test"))
                .andExpect(jsonPath("$.place.slug").value("new-test2"));

        var eventFromDb = eventService.getEventById(event.getId());

        Assertions.assertEquals(eventFromDb.getName(), "new-test");
        Assertions.assertEquals(eventFromDb.getDate(), LocalDateTime.of(2024, 11, 11, 0, 0, 1));
        Assertions.assertEquals(eventFromDb.getPlace().getId(), updatedPlace.getId());
    }

    @Test
    @DisplayName("Test update event but new place is not exist")
    public void testUpdateEventButPlaceIsNotExist() throws Exception {
        var place = placeService.createPlace("test", "test2");
        var event = eventService.createEvent("test",
                LocalDateTime.of(2024, 11, 11, 0, 0, 0),
                place.getId());

        var request = "{\"name\":\"new-test\",\"date\":\"11.11.2024 00:00:01\",\"placeId\": 99999999}";

        mockMvc.perform(put("/event/" + event.id)
                        .content(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        var eventFromDb = eventService.getEventById(event.getId());

        Assertions.assertEquals(eventFromDb.getName(), "test");
        Assertions.assertEquals(eventFromDb.getDate(), LocalDateTime.of(2024, 11, 11, 0, 0, 0));
        Assertions.assertEquals(eventFromDb.getPlace().getId(), place.getId());
    }

    @Test
    @DisplayName("Test get event")
    public void testGetEvent() throws Exception {
        var place = placeService.createPlace("test", "test2");
        var event = eventService.createEvent("test",
                LocalDateTime.of(2024, 11, 11, 0, 0, 0),
                place.getId());

        mockMvc.perform(get("/event/" + event.id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(event.id))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.date").value("11.11.2024 00:00:00"))
                .andExpect(jsonPath("$.place.id").value(place.getId()))
                .andExpect(jsonPath("$.place.name").value("test"))
                .andExpect(jsonPath("$.place.slug").value("test2"));
    }

    @Test
    @DisplayName("Test get event when it is not exist")
    public void testGetEventWhenNotExist() throws Exception {
        mockMvc.perform(get("/event/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test delete event")
    public void testDeleteEvent() throws Exception {
        var place = placeService.createPlace("test", "test2");
        var event = eventService.createEvent("test",
                LocalDateTime.of(2024, 11, 11, 0, 0, 0),
                place.getId());

        mockMvc.perform(delete("/event/" + event.id))
                .andExpect(status().isOk());

        Assertions.assertEquals(0, (long) eventService.count());
    }

    @Test
    @DisplayName("Test delete event when it not exist")
    public void testDeleteEventWhenItNotExist() throws Exception {
        mockMvc.perform(delete("/event/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test get all events")
    public void testGetAllEvents() throws Exception {
        var place = placeService.createPlace("test", "test2");
        var event = eventService.createEvent("test",
                LocalDateTime.of(2024, 11, 11, 0, 0, 0),
                place.getId());

        var place2 = placeService.createPlace("test2", "test3");
        var event2 = eventService.createEvent("test2",
                LocalDateTime.of(2024, 11, 14, 0, 0, 0),
                place2.getId());

        Assertions.assertEquals(2L, eventService.count());

        mockMvc.perform(get("/event"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(event.id))
                .andExpect(jsonPath("$[0].name").value("test"))
                .andExpect(jsonPath("$[0].date").value("11.11.2024 00:00:00"))
                .andExpect(jsonPath("$[0].placeId").value(place.getId()))
                .andExpect(jsonPath("$[1].id").value(event2.id))
                .andExpect(jsonPath("$[1].name").value("test2"))
                .andExpect(jsonPath("$[1].date").value("14.11.2024 00:00:00"))
                .andExpect(jsonPath("$[1].placeId").value(place2.getId()));
    }

    @Test
    @DisplayName("Test get all events when filter by name")
    public void testGetAllEventsWithFilterByName() throws Exception {
        var place = placeService.createPlace("test", "test2");
        var event = eventService.createEvent("test123",
                LocalDateTime.of(2024, 11, 11, 0, 0, 0),
                place.getId());

        var event2 = eventService.createEvent("test2",
                LocalDateTime.of(2024, 11, 14, 0, 0, 0),
                place.getId());

        Assertions.assertEquals(2L, eventService.count());

        mockMvc.perform(get("/event?name=test123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(event.id))
                .andExpect(jsonPath("$[0].name").value("test123"));
    }

    @Test
    @DisplayName("Test get all events when filter by place")
    public void testGetAllEventsWithFilterByPlace() throws Exception {
        var place = placeService.createPlace("test", "test2");
        var event = eventService.createEvent("test",
                LocalDateTime.of(2024, 11, 11, 0, 0, 0),
                place.getId());

        var place2 = placeService.createPlace("test2", "test3");
        var event2 = eventService.createEvent("test2",
                LocalDateTime.of(2024, 11, 14, 0, 0, 0),
                place2.getId());

        Assertions.assertEquals(2L, eventService.count());

        mockMvc.perform(get("/event?placeId=" + place2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(event2.id))
                .andExpect(jsonPath("$[0].placeId").value(place2.getId()));
    }

    @Test
    @DisplayName("Test get all events when filter by dates")
    public void testGetAllEventsWithFilterByDates() throws Exception {
        var place = placeService.createPlace("test", "test2");
        var event = eventService.createEvent("test",
                LocalDateTime.of(2024, 11, 11, 0, 0, 0),
                place.getId());

        var event2 = eventService.createEvent("test2",
                LocalDateTime.of(2024, 11, 14, 0, 0, 0),
                place.getId());

        var event3 = eventService.createEvent("test2",
                LocalDateTime.of(2024, 11, 20, 0, 0, 0),
                place.getId());

        Assertions.assertEquals(3L, eventService.count());

        mockMvc.perform(get("/event?fromDate=12.11.2024&toDate=19.11.2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(event2.id))
                .andExpect(jsonPath("$[0].date").value("14.11.2024 00:00:00"));
    }

    @Test
    @DisplayName("Test get all events when no objects after filter")
    public void testGetAllEventsWithNoResultsAfterFilter() throws Exception {
        var place = placeService.createPlace("test", "test2");
        var event = eventService.createEvent("test",
                LocalDateTime.of(2024, 11, 11, 0, 0, 0),
                place.getId());

        var event2 = eventService.createEvent("test2",
                LocalDateTime.of(2024, 11, 14, 0, 0, 0),
                place.getId());

        Assertions.assertEquals(2L, eventService.count());

        mockMvc.perform(get("/event?fromDate=30.11.2024"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
