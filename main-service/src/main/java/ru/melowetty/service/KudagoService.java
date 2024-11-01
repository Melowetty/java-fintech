package ru.melowetty.service;

import ru.melowetty.model.Category;
import ru.melowetty.model.EventDto;
import ru.melowetty.model.Location;

import java.time.LocalDate;
import java.util.List;

public interface KudagoService {
    List<Category> getCategories();

    List<Location> getLocations();

    List<EventDto> getEvents(LocalDate dateFrom, LocalDate dateTo, int page);
}
