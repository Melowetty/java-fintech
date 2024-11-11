package ru.melowetty.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.melowetty.command.InitCommand;
import ru.melowetty.controller.request.PlacePutRequest;
import ru.melowetty.event.impl.LocationEventManager;
import ru.melowetty.exception.EntityNotFoundException;
import ru.melowetty.model.Location;
import ru.melowetty.repository.LocationRepository;
import ru.melowetty.service.impl.LocationServiceImpl;
import ru.melowetty.service.impl.LocationTransactionService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LocationServiceImplTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private LocationTransactionService transactionService;

    @Mock
    private LocationEventManager eventManager;

    @Mock
    private InitCommand command;

    @InjectMocks
    private LocationServiceImpl locationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void getLocations_returnsAllLocations() {
        List<Location> locations = getLocations();
        Mockito.when(locationRepository.findAll()).thenReturn(locations);

        List<Location> result = locationService.getLocations();

        assertEquals(locations.size(), result.size());
        assertEquals(locations, result);
    }

    @Test
    public void getLocationBySlug_existingSlug_returnsLocation() {
        Location location = getLocations().get(0);
        Mockito.when(locationRepository.existsById(location.getSlug())).thenReturn(true);
        Mockito.when(locationRepository.findById(location.getSlug())).thenReturn(location);

        Location result = locationService.getLocationBySlug(location.getSlug());

        assertEquals(location, result);
    }

    @Test
    public void getLocationBySlug_nonExistingSlug_throwsException() {
        String nonExistingSlug = "non-existing-slug";
        Mockito.when(locationRepository.existsById(nonExistingSlug)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> locationService.getLocationBySlug(nonExistingSlug));
    }

    @Test
    public void createLocation_validData_createsLocation() {
        String slug = "new-slug";
        String name = "new-name";
        Location location = new Location();
        location.setSlug(slug);
        location.setName(name);
        Mockito.when(locationRepository.create(Mockito.any(Location.class))).thenReturn(location);

        Location result = locationService.createLocation(slug, name);

        assertEquals(slug, result.getSlug());
        assertEquals(name, result.getName());
    }

    @Test
    public void createLocation_rollback_when_save_throws_exception() {
        Mockito.when(locationRepository.create(Mockito.any(Location.class))).thenThrow(RuntimeException.class);

        var location = locationService.createLocation("test", "test");

        assertNull(location);

        Mockito.verify(transactionService, Mockito.times(1)).rollback();
    }

    @Test
    public void updateLocation_existingSlug_updatesLocation() {
        String slug = "existing-slug";

        Location location = new Location();

        location.setSlug(slug);
        location.setName("updated-name");

        Mockito.when(locationRepository.existsById(slug)).thenReturn(true);
        Mockito.when(locationRepository.update(Mockito.any(Location.class))).thenReturn(location);

        Location result = locationService.updateLocation(slug, "updated-name");

        assertEquals(slug, result.getSlug());
        assertEquals("updated-name", result.getName());
    }

    @Test
    public void updateLocation_nonExistingSlug_throwsException() {
        String nonExistingSlug = "non-existing-slug";

        Mockito.when(locationRepository.existsById(nonExistingSlug)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> locationService.updateLocation(nonExistingSlug, "updated-name"));
    }

    @Test
    public void deleteLocation_existingSlug_deletesLocation() {
        String slug = "existing-slug";
        Mockito.when(locationRepository.existsById(slug)).thenReturn(true);

        locationService.deleteLocation(slug);

        Mockito.verify(locationRepository, Mockito.times(1)).removeById(slug);
    }

    @Test
    public void deleteLocation_nonExistingSlug_throwsException() {
        String nonExistingSlug = "non-existing-slug";
        Mockito.when(locationRepository.existsById(nonExistingSlug)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> locationService.deleteLocation(nonExistingSlug));
    }

    private List<Location> getLocations() {
        return List.of(
                new Location("slug1", "name1"),
                new Location("slug2", "name2"),
                new Location("slug3", "name3")
        );
    }
}