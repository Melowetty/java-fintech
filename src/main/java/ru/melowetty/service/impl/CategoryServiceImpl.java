package ru.melowetty.service.impl;

import org.springframework.stereotype.Service;
import ru.melowetty.controller.request.CategoryPutRequest;
import ru.melowetty.exception.EntityNotFoundException;
import ru.melowetty.model.Category;
import ru.melowetty.repository.CategoryRepository;
import ru.melowetty.service.CategoryService;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(int id) {
        if(categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Категория с таким идентификатором не найдена!");
        }
        return categoryRepository.findById(id);
    }

    @Override
    public Category createCategory(String slug, String name) {
        var category = new Category();
        category.setName(name);
        category.setSlug(slug);
        return categoryRepository.create(category);
    }

    @Override
    public Category updateCategory(int id, CategoryPutRequest request) {
        if(categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Категория с таким идентификатором не найдена!");
        }
        var category = new Category();
        category.setId(id);
        category.setSlug(request.slug);
        category.setName(request.name);
        return categoryRepository.update(category);
    }

    @Override
    public void deleteCategoryById(int id) {
        if(categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Категория с таким идентификатором не найдена!");
        }

        categoryRepository.removeById(id);
    }
}
