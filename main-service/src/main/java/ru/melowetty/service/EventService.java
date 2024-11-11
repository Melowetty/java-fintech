package ru.melowetty.service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.melowetty.entity.Event;
import ru.melowetty.entity.Place;
import ru.melowetty.entity.Event_;
import ru.melowetty.entity.Place_;
import ru.melowetty.exception.EntityNotFoundException;
import ru.melowetty.exception.RelatedEntityNotFoundException;
import ru.melowetty.repository.EventRepository;
import ru.melowetty.repository.PlaceRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

    public List<Event> filterAllEvents(String name, Long placeId, LocalDateTime fromDate, LocalDateTime toDate) {
        var specifications = new ArrayList<Specification<Event>>();

        if (name != null) {
            Specification<Event> filterByName = ((root, query, criteriaBuilder) ->
                    criteriaBuilder.like(root.get(Event_.NAME), "%" + name + "%"));
            specifications.add(filterByName);
        }

        if (placeId != null) {
            Specification<Event> filterByPlace = (((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get(Event_.PLACE).get(Place_.ID), placeId)));
            specifications.add(filterByPlace);
        }

        if (fromDate != null) {
            Specification<Event> filterByFromDate = ((root, query, criteriaBuilder) ->
                    criteriaBuilder.greaterThanOrEqualTo(root.get(Event_.DATE), fromDate));
            specifications.add(filterByFromDate);
        }

        if (toDate != null) {
            Specification<Event> filterByToDate = ((root, query, criteriaBuilder) ->
                    criteriaBuilder.lessThanOrEqualTo(root.get(Event_.DATE), toDate));
            specifications.add(filterByToDate);
        }

        return eventRepository.findAll(Specification.allOf(specifications));
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

    public Long count() {
        return eventRepository.count();
    }
}
