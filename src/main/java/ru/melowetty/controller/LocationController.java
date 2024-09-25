package ru.melowetty.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.melowetty.controller.request.LocationCreateRequest;
import ru.melowetty.controller.request.LocationPutRequest;
import ru.melowetty.model.Location;
import ru.melowetty.service.LocationService;

import java.util.List;

@RestController
@RequestMapping("/v1/locations")
public class LocationController {
    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public List<Location> getLocations() {
        return locationService.getLocations();
    }

    @GetMapping("/{slug}")
    public Location getLocationBySlug(@PathVariable String slug) {
        return locationService.getLocationBySlug(slug);
    }

    @DeleteMapping("/{slug}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLocation(@PathVariable String slug) {
        locationService.deleteLocation(slug);
    }

    @PostMapping
    public Location createLocation(@RequestBody LocationCreateRequest request) {
        return locationService.createLocation(request.slug, request.name);
    }

    @PutMapping("/{slug}")
    public Location updateLocation(@PathVariable String slug, @RequestBody LocationPutRequest request) {
        return locationService.updateLocation(slug, request);
    }
}
