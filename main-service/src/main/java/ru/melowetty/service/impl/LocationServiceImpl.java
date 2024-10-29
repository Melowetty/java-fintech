package ru.melowetty.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.melowetty.annotation.Timed;
import ru.melowetty.command.InitCommand;
import ru.melowetty.controller.request.LocationPutRequest;
import ru.melowetty.exception.EntityNotFoundException;
import ru.melowetty.model.Location;
import ru.melowetty.repository.LocationRepository;
import ru.melowetty.service.LocationService;

import java.util.List;

@Service
@Slf4j
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    @Qualifier("location_init")
    @Autowired
    @Lazy
    private InitCommand initCommand;

    public LocationServiceImpl(
            LocationRepository locationRepository
    ) {
        this.locationRepository = locationRepository;
        log.info("LocationServiceImpl created");
    }

    @EventListener(ApplicationReadyEvent.class)
    @Timed
    public void initialize() {
        initCommand.execute();
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
