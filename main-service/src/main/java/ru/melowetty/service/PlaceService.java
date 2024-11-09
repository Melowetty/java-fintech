package ru.melowetty.service;

import org.springframework.stereotype.Service;
import ru.melowetty.repository.PlaceRepository;

@Service
public class PlaceService {
    private final PlaceRepository placeRepository;

    public PlaceService(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }


}
