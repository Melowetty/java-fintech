package ru.melowetty.service;

import org.springframework.stereotype.Service;
import ru.melowetty.entity.Place;
import ru.melowetty.exception.EntityNotFoundException;
import ru.melowetty.repository.PlaceRepository;

import java.util.List;

@Service
public class PlaceService {
    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public Place createPlace(String name, String slug) {
        var place = new Place();

        place.setName(name);
        place.setSlug(slug);
        return placeRepository.save(place);
    }

    public Place getPlaceById(Long id) {
        return placeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Место с таким ID не найдено!"));
    }

    public void deletePlaceById(Long id) {
        if (!placeRepository.existsById(id)) {
            throw new EntityNotFoundException("Место с таким ID не найдено!");
        }

        placeRepository.deleteById(id);
    }

    public List<Place> getAllPlaces() {
        return placeRepository.findAll();
    }

    public Place updatePlace(Long id, String name, String slug) {
        var place = getPlaceById(id);

        place.setName(name);
        place.setSlug(slug);

        return placeRepository.save(place);
    }
}
