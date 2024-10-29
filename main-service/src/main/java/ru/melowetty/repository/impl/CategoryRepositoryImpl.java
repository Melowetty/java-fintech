package ru.melowetty.repository.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.melowetty.event.EventListener;
import ru.melowetty.event.EventType;
import ru.melowetty.model.Category;
import ru.melowetty.repository.CategoryRepository;

@Repository
@Slf4j
public class CategoryRepositoryImpl extends BaseRepositoryImpl<Category, Integer> implements
        CategoryRepository, EventListener<Category> {
    @Override
    public Category create(Category entity) {
        entity.setId(count() + 1);
        return super.create(entity);
    }

    @Override
    protected Integer getIndexFromEntity(Category entity) {
        return entity.getId();
    }

    @Override
    public void handleUpdate(EventType eventType, Category event) {
        log.info("Обновилась категория {}, тип ивента: {}", event, eventType);
    }
}
