package ru.melowetty.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.melowetty.entity.Place;
import ru.melowetty.exception.EntityNotFoundException;
import ru.melowetty.repository.PlaceRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class PlaceServiceTest {
    @Spy
    @InjectMocks
    private PlaceService placeService;

    @Mock
    private PlaceRepository placeRepository;

    @Test
    public void testGetByIdIfItExist() {
        var place = new Place();

        place.setId(1L);
        place.setName("test");
        place.setSlug("test2");

        Mockito.when(placeRepository.getPlaceById(1L)).thenReturn(Optional.of(place));

        var actual = placeService.getPlaceById(1L);

        Assertions.assertEquals(place, actual);
    }

    @Test
    public void testGetByIdIfItNotExist() {
        Mockito.when(placeRepository.getPlaceById(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            placeService.getPlaceById(1L);
        });
    }

    @Test
    public void testCreatePlace() {
        var place = new Place();

        place.setId(1L);
        place.setName("test");
        place.setSlug("test2");

        Mockito.when(placeRepository.save(ArgumentMatchers.argThat((arg) ->
            arg.name.equals("test") && arg.slug.equals("test2")
        ))).thenReturn(place);

        var actual = placeService.createPlace("test", "test2");

        Assertions.assertEquals(actual, place);
    }

    @Test
    public void testGetAllPlaces() {
        var place = new Place();

        place.setId(1L);
        place.setName("test");
        place.setSlug("test2");

        var place2 = new Place();
        place2.setId(2L);
        place2.setName("test");
        place2.setSlug("test2");

        Mockito.when(placeRepository.findAll()).thenReturn(List.of(place, place2));

        var actual = placeService.getAllPlaces();

        Assertions.assertEquals(2, actual.size());
    }

    @Test
    public void testGetCount() {
        long count = 1;

        Mockito.when(placeRepository.count()).thenReturn(count);

        var actual = placeService.count();

        Assertions.assertEquals(count, actual);
    }

    @Test
    public void testDeleteIfExists() {
        Mockito.when(placeRepository.existsById(1L)).thenReturn(true);

        placeService.deletePlaceById(1L);

        Mockito.verify(placeRepository, Mockito.times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteIfNotExists() {
        Mockito.when(placeRepository.existsById(1L)).thenReturn(false);

        Assertions.assertThrows(EntityNotFoundException.class, () -> {
            placeService.deletePlaceById(1L);
        });

        Mockito.verify(placeRepository, Mockito.times(0)).deleteById(1L);
    }

    @Test
    public void testUpdate() {
        var place = new Place();

        place.setId(1L);
        place.setName("test");
        place.setSlug("test2");

        var newPlace = new Place();

        newPlace.setId(1L);
        newPlace.setName("new-test");
        newPlace.setSlug("new-test2");

        Mockito.doReturn(place)
                        .when(placeService).getPlaceById(1L);

        Mockito.when(placeRepository.save(ArgumentMatchers.argThat((arg) ->
                arg.name.equals("new-test") && arg.slug.equals("new-test2") && arg.getId() == 1L))).thenReturn(newPlace);

        var actual = placeService.updatePlace(1L, "new-test", "new-test2");

        Assertions.assertEquals(newPlace, actual);
    }
}
