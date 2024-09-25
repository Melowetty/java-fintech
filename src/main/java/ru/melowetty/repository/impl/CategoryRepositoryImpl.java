package ru.melowetty.repository.impl;

import org.springframework.stereotype.Repository;
import ru.melowetty.model.Category;
import ru.melowetty.repository.CategoryRepository;

@Repository
public class CategoryRepositoryImpl extends BaseRepositoryImpl<Category, Integer> implements CategoryRepository {
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
