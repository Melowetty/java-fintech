package ru.melowetty.service;

import org.springframework.stereotype.Service;
import ru.melowetty.entity.Event;
import ru.melowetty.entity.Place;
import ru.melowetty.exception.EntityNotFoundException;
import ru.melowetty.exception.RelatedEntityNotFoundException;
import ru.melowetty.repository.EventRepository;
import ru.melowetty.repository.PlaceRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final PlaceRepository placeRepository;

    public EventService(EventRepository eventRepository, PlaceRepository placeRepository) {
        this.eventRepository = eventRepository;
        this.placeRepository = placeRepository;
    }

    public Event createEvent(String name, LocalDateTime date, Long placeId) {
        var event = new Event();
        var place = getPlaceById(placeId);

        event.setName(name);
        event.setPlace(place);
        event.setDate(date);
        return eventRepository.save(event);
    }

    public Event getEventById(Long id) {
        return eventRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Событие с таким ID не найдено!"));
    }

    public void deleteEventById(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new EntityNotFoundException("Событие с таким ID не найдено!");
        }

        eventRepository.deleteById(id);
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Event updateEvent(Long id, String name, LocalDateTime date, Long placeId) {
        var event = getEventById(id);
        var place = getPlaceById(placeId);

        event.setName(name);
        event.setDate(date);
        event.setPlace(place);

        return eventRepository.save(event);
    }

    public Place getPlaceById(Long id) {
        return placeRepository.getPlaceById(id)
                .orElseThrow(() -> new RelatedEntityNotFoundException("Место с таким ID не найдено!"));
    }
}
