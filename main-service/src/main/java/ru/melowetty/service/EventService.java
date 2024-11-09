package ru.melowetty.service;

import org.springframework.stereotype.Service;
import ru.melowetty.repository.EventRepository;

@Service
public class EventService {
    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }
}
