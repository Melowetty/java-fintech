package ru.melowetty.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
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
import ru.melowetty.annotation.Timed;
import ru.melowetty.controller.request.PlaceCreateRequest;
import ru.melowetty.controller.request.PlacePutRequest;
import ru.melowetty.entity.Place;
import ru.melowetty.model.Location;
import ru.melowetty.service.PlaceService;

import java.util.List;

@RestController
@RequestMapping("/v1/place")
@Timed
@Valid
public class PlaceController {
    private final PlaceService placeService;

    public PlaceController(PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping(produces = "application/json")
    public List<Place> getPlaces() {
        return placeService.getAllPlaces();
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public Place getLocationBySlug(@PathVariable @NotNull Long id) {
        return placeService.getPlaceById(id);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePlace(@PathVariable @NotNull Long id) {
        placeService.deletePlaceById(id);
    }

    @PostMapping(produces = "application/json")
    public Place createPlace(@RequestBody @NotNull PlaceCreateRequest request) {
        return placeService.createPlace(request.name, request.slug);
    }

    @PutMapping(path = "/{id}", produces = "application/json")
    public Place updateLocation(@PathVariable @NotNull Long id, @RequestBody @NotNull PlacePutRequest request) {
        return placeService.updatePlace(id, request.name, request.slug);
    }
}
