package ru.melowetty.service.impl;

import org.springframework.stereotype.Service;
import ru.melowetty.controller.request.LocationPutRequest;
import ru.melowetty.exception.EntityNotFoundException;
import ru.melowetty.model.Location;
import ru.melowetty.repository.LocationRepository;
import ru.melowetty.service.LocationService;

import java.util.List;

@Service
public class LocationServiceImpl implements LocationService {
    private LocationRepository locationRepository;

    public LocationServiceImpl(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public List<Location> getLocations() {
        return locationRepository.findAll();
    }

    @Override
    public Location getLocationBySlug(String slug) {
        if (locationRepository.existsById(slug)) {
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
        if (locationRepository.existsById(slug)) {
            throw new EntityNotFoundException("Локация с таким идентификатором не найдена!");
        }

        var location = new Location();
        location.setSlug(slug);
        location.setName(request.name);
        return locationRepository.update(location);
    }

    @Override
    public void deleteLocation(String slug) {
        if (locationRepository.existsById(slug)) {
            throw new EntityNotFoundException("Локация с таким идентификатором не найдена!");
        }

        locationRepository.removeById(slug);
    }
}
