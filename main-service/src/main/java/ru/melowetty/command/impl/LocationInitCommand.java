package ru.melowetty.command.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.melowetty.command.InitCommand;
import ru.melowetty.service.KudagoService;
import ru.melowetty.service.LocationService;

@Component
@Slf4j
@Qualifier("location_init")
public class LocationInitCommand implements InitCommand {
    private final LocationService locationService;
    private final KudagoService kudagoService;

    public LocationInitCommand(LocationService locationService, KudagoService kudagoService) {
        this.locationService = locationService;
        this.kudagoService = kudagoService;
    }

    @Override
    public void execute() {
        log.info("Инициализация городов запущена");
        var locations = kudagoService.getLocations();
        for (var location : locations) {
            locationService.createLocation(location.slug, location.name);
        }
        log.info("Инициализация городов окончена, теперь городов: {}", locationService.getLocations().size());
    }
}
