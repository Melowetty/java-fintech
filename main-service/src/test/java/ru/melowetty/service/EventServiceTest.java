package ru.melowetty.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.melowetty.entity.Event;
import ru.melowetty.entity.Place;
import ru.melowetty.exception.EntityNotFoundException;
import ru.melowetty.exception.RelatedEntityNotFoundException;
import ru.melowetty.repository.EventRepository;
import ru.melowetty.repository.PlaceRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {
    @Spy
    @InjectMocks
    private EventService eventService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Test
    public void testGetByIdIfItExist() {
        var event = new Event();
        var place = new Place();

        place.setId(1L);
        place.setName("test");
        place.setSlug("test2");

        event.setId(1L);
        event.setDate(LocalDateTime.of(2024, 10, 10, 10, 10, 10));
        event.setName("test");
        event.setPlace(place);


        Mockito.when(eventRepository.findById(1L)).thenReturn(Optional.of(event));

        var actual = eventService.getEventById(1L);

        Assertions.assertEquals(event, actual);
    }

    @Test
    public void testGetByIdIfItNotExist() {
        Mockito.when(eventRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            eventService.getEventById(1L);
        });
    }

    @Test
    public void testGetPlaceByIdIfItExist() {
        var place = new Place();

        place.setId(1L);
        place.setName("test");
        place.setSlug("test2");

        Mockito.when(placeRepository.findById(1L)).thenReturn(Optional.of(place));

        var actual = eventService.getPlaceById(1L);

        Assertions.assertEquals(place, actual);
    }

    @Test
    public void testGetPlaceByIdIfItNotExist() {
        Mockito.when(placeRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(RelatedEntityNotFoundException.class, () -> {
            eventService.getPlaceById(1L);
        });
    }

    @Test
    public void testCreateEvent() {
        var event = new Event();
        var place = new Place();

        place.setId(1L);
        place.setName("test");
        place.setSlug("test2");

        var date = LocalDateTime.of(2024, 10, 10, 10, 10, 10);

        event.setId(1L);
        event.setDate(date);
        event.setName("test");
        event.setPlace(place);

        Mockito.doReturn(place)
                .when(eventService).getPlaceById(1L);

        Mockito.when(eventRepository.save(ArgumentMatchers.argThat((arg) ->
                arg.name.equals("test") && arg.place.getId() == 1L && arg.date.equals(date)
        ))).thenReturn(event);

        var actual = eventService.createEvent("test", date, 1L);

        Assertions.assertEquals(event, actual);
    }

    @Test
    public void testGetAllEvents() {
        Mockito.when(eventRepository.findAll()).thenReturn(List.of(Mockito.mock(Event.class), Mockito.mock(Event.class)));

        var actual = eventService.getAllEvents();

        Assertions.assertEquals(2, actual.size());
    }

    @Test
    public void testGetCount() {
        long count = 1;

        Mockito.when(eventRepository.count()).thenReturn(count);

        var actual = eventService.count();

        Assertions.assertEquals(count, actual);
    }

    @Test
    public void testDeleteIfExists() {
        Mockito.when(eventRepository.existsById(1L)).thenReturn(true);

        eventService.deleteEventById(1L);

        Mockito.verify(eventRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteIfNotExists() {
        Mockito.when(eventRepository.existsById(1L)).thenReturn(false);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            eventService.deleteEventById(1L);
        });

        Mockito.verify(eventRepository, Mockito.times(0)).deleteById(1L);
    }

    @Test
    public void testUpdate() {
        var place = new Place();

        place.setId(1L);
        place.setName("test");
        place.setSlug("test2");

        var event = new Event();

        var date = LocalDateTime.of(2024, 10, 10, 10, 10, 10);

        event.setId(1L);
        event.setDate(date);
        event.setName("test");
        event.setPlace(place);

        var newEvent = new Event();

        var newDate = LocalDateTime.of(2024, 10, 10, 10, 10, 11);

        newEvent.setId(1L);
        newEvent.setDate(newDate);
        newEvent.setName("new-test");
        newEvent.setPlace(place);

        Mockito.doReturn(event)
                .when(eventService).getEventById(1L);

        Mockito.doReturn(place)
                .when(eventService).getPlaceById(1L);

        Mockito.when(eventRepository.save(ArgumentMatchers.argThat((arg) ->
                        arg.name.equals("new-test") && arg.place.getId() == 1L && arg.getId() == 1L
                                && arg.date.equals(newDate))
                )
        ).thenReturn(newEvent);

        var actual = eventService.updateEvent(1L, "new-test", newDate, place.getId());

        Assertions.assertEquals(newEvent, actual);
    }
}
