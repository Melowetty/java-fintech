package ru.melowetty.command.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.melowetty.command.InitCommand;
import ru.melowetty.repository.LocationRepository;
import ru.melowetty.service.KudagoService;

@Component
@Slf4j
@Qualifier("location_init")
public class LocationInitCommand implements InitCommand {
    private final LocationRepository locationRepository;
    private final KudagoService kudagoService;

    public LocationInitCommand(LocationRepository locationRepository, KudagoService kudagoService) {
        this.locationRepository = locationRepository;
        this.kudagoService = kudagoService;
    }

    @Override
    public void execute() {
        log.info("Инициализация городов запущена");
        var locations = kudagoService.getLocations();
        for (var location : locations) {
            locationRepository.create(location);
        }
        log.info("Инициализация городов окончена, теперь городов: {}", locationRepository.findAll().size());
    }
}
