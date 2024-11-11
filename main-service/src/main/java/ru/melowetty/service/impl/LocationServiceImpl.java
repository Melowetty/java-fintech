package ru.melowetty.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melowetty.event.EventType;
import ru.melowetty.event.impl.LocationEventManager;
import ru.melowetty.exception.EntityNotFoundException;
import ru.melowetty.model.Location;
import ru.melowetty.repository.LocationRepository;
import ru.melowetty.service.LocationService;

import java.util.List;

@Service
@Slf4j
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;
    private final LocationEventManager eventManager;
    private final LocationTransactionService transactionService;

    public LocationServiceImpl(
            LocationRepository locationRepository, LocationEventManager eventManager,
            LocationTransactionService transactionService
    ) {
        this.locationRepository = locationRepository;
        this.eventManager = eventManager;
        this.transactionService = transactionService;
        log.info("LocationServiceImpl created");
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
        try {
            var location = new Location();
            location.setName(name);
            location.setSlug(slug);
            var newLocation = locationRepository.create(location);
            eventManager.notify(EventType.CREATED, newLocation);
            return newLocation;
        } catch (RuntimeException e) {
            transactionService.rollback();
            return null;
        }
    }

    @Override
    public Location updateLocation(String slug, String name) {
        if (!locationRepository.existsById(slug)) {
            throw new EntityNotFoundException("Локация с таким идентификатором не найдена!");
        }

        var location = new Location();
        location.setSlug(slug);
        location.setName(name);
        var newLocation = locationRepository.update(location);
        eventManager.notify(EventType.CHANGED, newLocation);
        return newLocation;
    }

    @Override
    public void deleteLocation(String slug) {
        if (!locationRepository.existsById(slug)) {
            throw new EntityNotFoundException("Локация с таким идентификатором не найдена!");
        }
        var location = getLocationBySlug(slug);
        locationRepository.removeById(slug);
        eventManager.notify(EventType.DELETED, location);
    }
}
