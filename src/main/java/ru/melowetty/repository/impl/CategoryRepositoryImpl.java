package ru.melowetty.repository.impl;

import org.springframework.stereotype.Repository;
import ru.melowetty.model.Category;
import ru.melowetty.repository.CategoryRepository;

@Repository
public class CategoryRepositoryImpl extends BaseRepositoryImpl<Category, Integer> implements CategoryRepository {
    @Override
    public Category create(Category entity) {
        entity.setId(count() + 1);
        return super.create(entity);
    }

    @Override
    protected Integer getIndexFromEntity(Category entity) {
        return entity.getId();
    }
}
