package ru.melowetty.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.melowetty.controller.request.LocationPutRequest;
import ru.melowetty.model.Location;
import ru.melowetty.service.LocationService;

import java.util.Arrays;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LocationController.class)
public class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LocationService locationService;

    @Test
    public void getLocations_returnsListOfLocations() throws Exception {
        var locations = Arrays.asList(new Location("slug1", "SPB"), new Location("slug2", "MOSCOW"));
        when(locationService.getLocations()).thenReturn(locations);

        mockMvc.perform(get("/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("slug1"))
                .andExpect(jsonPath("$[0].name").value("SPB"))
                .andExpect(jsonPath("$[1].slug").value("slug2"))
                .andExpect(jsonPath("$[1].name").value("MOSCOW"));
    }

    @Test
    public void getLocationBySlug_existingSlug_returnsLocation() throws Exception {
        var location = new Location("slug1", "SPB");
        when(locationService.getLocationBySlug("slug1")).thenReturn(location);

        mockMvc.perform(get("/v1/locations/slug1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("slug1"))
                .andExpect(jsonPath("$.name").value("SPB"));
    }

    @Test
    public void updateLocation_existingSlug_returnsUpdatedLocation() throws Exception {
        LocationPutRequest request = new LocationPutRequest("New SPB");
        var location = new Location("slug", "New SPB");
        when(locationService.updateLocation("slug", request)).thenReturn(location);

        mockMvc.perform(put("/v1/locations/slug")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"New SPB\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("slug"))
                .andExpect(jsonPath("$.name").value("New SPB"));
    }

    @Test
    public void create_Location() throws Exception {
        var location = new Location("slug", "name");
        when(locationService.createLocation("slug", "name")).thenReturn(location);

        mockMvc.perform(post("/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\": \"slug\", \"name\": \"name\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("slug"))
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    public void deleteLocationBySlug_existingSlug_deletesLocation() throws Exception {
        doNothing().when(locationService).deleteLocation("test");

        mockMvc.perform(delete("/v1/locations/test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(locationService, times(1)).deleteLocation("test");
    }
}
