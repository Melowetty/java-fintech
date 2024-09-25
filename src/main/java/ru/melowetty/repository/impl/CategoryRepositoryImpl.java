package ru.melowetty.repository.impl;

import org.springframework.stereotype.Repository;
import ru.melowetty.model.Category;

@Repository
public class CategoryRepositoryImpl extends BaseRepositoryImpl<Category, Integer> {
    @Override
    public Category create(Category entity) {
        var category = super.create(entity);
        category.setId(count());

        return category;
    }

    @Override
    Integer getIndexFromEntity(Category entity) {
        return entity.getId();
    }
}
