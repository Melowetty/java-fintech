package ru.melowetty.service;

import ru.melowetty.controller.request.LocationPutRequest;
import ru.melowetty.model.Location;

import java.util.List;

public interface LocationService {
    List<Location> getLocations();

    Location getLocationBySlug(String slug);

    Location createLocation(String slug, String name);

    Location updateLocation(String slug, LocationPutRequest request);

    void deleteLocation(String slug);
}
