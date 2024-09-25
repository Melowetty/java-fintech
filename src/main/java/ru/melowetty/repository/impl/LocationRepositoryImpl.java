package ru.melowetty.repository.impl;

import org.springframework.stereotype.Repository;
import ru.melowetty.model.Location;

@Repository
public class LocationRepositoryImpl extends BaseRepositoryImpl<Location, String> {
    @Override
    String getIndexFromEntity(Location entity) {
        return entity.slug;
    }
}
