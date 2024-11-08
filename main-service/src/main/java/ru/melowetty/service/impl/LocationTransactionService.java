package ru.melowetty.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melowetty.event.EventListener;
import ru.melowetty.event.EventType;
import ru.melowetty.model.Location;
import ru.melowetty.repository.LocationRepository;
import ru.melowetty.service.BackupService;

@Service
@Slf4j
public class LocationTransactionService extends BackupService implements EventListener<Location> {
    private final LocationRepository locationRepository;

    public LocationTransactionService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    @Override
    public void handleUpdate(EventType eventType, Location event) {
        addChange(locationRepository);
        log.info("Записано новое состояние локаций");
    }

    @Override
    public void rollback() {
        super.rollback();
        log.info("Был вызван откат операции");
    }
}
