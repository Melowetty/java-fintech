package ru.melowetty.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.melowetty.annotation.Timed;
import ru.melowetty.controller.request.LocationPutRequest;
import ru.melowetty.exception.EntityNotFoundException;
import ru.melowetty.model.Location;
import ru.melowetty.repository.LocationRepository;
import ru.melowetty.service.KudagoService;
import ru.melowetty.service.LocationService;

import java.util.List;

@Service
@Slf4j
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final KudagoService kudagoService;

    public LocationServiceImpl(LocationRepository locationRepository, KudagoService kudagoService) {
        this.locationRepository = locationRepository;
        this.kudagoService = kudagoService;
        log.info("LocationServiceImpl created");
    }

    @EventListener(ApplicationReadyEvent.class)
    @Timed
    public void initialize() {
        log.info("Инициализация городов запущена");
        var locations = kudagoService.getLocations();
        for (var location : locations) {
            locationRepository.create(location);
        }
        log.info("Инициализация городов окончена, теперь городов: {}", locationRepository.findAll().size());
    }

    @Override
    public List<Location> getLocations() {
        return locationRepository.findAll();
    }

    @Override
    public Location getLocationBySlug(String slug) {
        if (!locationRepository.existsById(slug)) {
            throw new EntityNotFoundException("Локация с таким идентификатором не найдена!");
        }

        return locationRepository.findById(slug);
    }

    @Override
    public Location createLocation(String slug, String name) {
        var location = new Location();
        location.setName(name);
        location.setSlug(slug);
        return locationRepository.create(location);
    }

    @Override
    public Location updateLocation(String slug, LocationPutRequest request) {
        if (!locationRepository.existsById(slug)) {
            throw new EntityNotFoundException("Локация с таким идентификатором не найдена!");
        }

        var location = new Location();
        location.setSlug(slug);
        location.setName(request.name);
        return locationRepository.update(location);
    }

    @Override
    public void deleteLocation(String slug) {
        if (!locationRepository.existsById(slug)) {
            throw new EntityNotFoundException("Локация с таким идентификатором не найдена!");
        }

        locationRepository.removeById(slug);
    }
}
