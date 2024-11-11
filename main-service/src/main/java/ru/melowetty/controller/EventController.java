package ru.melowetty.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.melowetty.controller.request.EventCreateRequest;
import ru.melowetty.controller.request.EventPutRequest;
import ru.melowetty.entity.Event;
import ru.melowetty.service.EventService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("event")
@Valid
public class EventController {
    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public List<Event> getAllEvents(
            @RequestParam(required = false)
            String name,
            @RequestParam(required = false)
            Long placeId,
            @RequestParam(required = false)
            LocalDate fromDate,
            @RequestParam(required = false)
            LocalDate toDate
    ) {
        return eventService.filterAllEvents(name, placeId, fromDate.atStartOfDay(), toDate.atTime(23, 59, 59));
    }

    @GetMapping("/{id}")
    public Event getEvent(@PathVariable Long id) {
       return eventService.getEventById(id);
    }

    @PostMapping
    public Event createEvent(@RequestBody EventCreateRequest request) {
        return eventService.createEvent(
                request.name,
                request.date,
                request.placeId
        );
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable Long id) {
        eventService.deleteEventById(id);
    }

    @PutMapping("/{id}")
    public Event updateEvent(@PathVariable Long id, @RequestBody EventPutRequest request) {
        return eventService.updateEvent(id, request.name, request.date, request.placeId);
    }
}
