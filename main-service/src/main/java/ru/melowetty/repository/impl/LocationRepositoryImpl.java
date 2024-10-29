package ru.melowetty.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.melowetty.event.EventListener;
import ru.melowetty.event.EventType;
import ru.melowetty.model.Location;
import ru.melowetty.repository.LocationRepository;

@Repository
@Slf4j
public class LocationRepositoryImpl extends BaseRepositoryImpl<Location, String> implements
        LocationRepository, EventListener<Location> {
    @Override
    protected String getIndexFromEntity(Location entity) {
        return entity.slug;
    }

    @Override
    public void handleUpdate(EventType eventType, Location event) {
        log.info("Обновилось место {}, тип ивента: {}", event, eventType);
    }
}
