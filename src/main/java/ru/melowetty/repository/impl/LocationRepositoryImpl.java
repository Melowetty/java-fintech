package ru.melowetty.repository.impl;

import org.springframework.stereotype.Repository;
import ru.melowetty.model.Location;
import ru.melowetty.repository.LocationRepository;

@Repository
public class LocationRepositoryImpl extends BaseRepositoryImpl<Location, String> implements LocationRepository {
    @Override
    String getIndexFromEntity(Location entity) {
        return entity.slug;
    }
}
