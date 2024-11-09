package ru.melowetty.service;

import ru.melowetty.controller.request.PlacePutRequest;
import ru.melowetty.model.Location;

import java.util.List;

public interface LocationService {
    List<Location> getLocations();

    Location getLocationBySlug(String slug);

    Location createLocation(String slug, String name);

    Location updateLocation(String slug, PlacePutRequest request);

    void deleteLocation(String slug);
}
