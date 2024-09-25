package ru.melowetty.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.melowetty.annotation.Timed;
import ru.melowetty.controller.request.CategoryPutRequest;
import ru.melowetty.exception.EntityNotFoundException;
import ru.melowetty.model.Category;
import ru.melowetty.repository.CategoryRepository;
import ru.melowetty.service.CategoryService;
import ru.melowetty.service.KudagoService;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final KudagoService kudagoService;

    public CategoryServiceImpl(CategoryRepository categoryRepository, KudagoService kudagoService) {
        this.categoryRepository = categoryRepository;
        this.kudagoService = kudagoService;
        log.info("CategoryServiceImpl created");
    }

    @EventListener(ApplicationReadyEvent.class)
    @Timed
    public void initialize() {
        log.info("Инициализация категорий запущена");
        var categories = kudagoService.getCategories();
        for (var category : categories) {
            categoryRepository.create(category);
        }
        log.info("Инициализация категорий окончена, теперь категорий: {}", categoryRepository.findAll().size());
    }

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(int id) {
        if(!categoryRepository.existsById(id)) {
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
        if(!categoryRepository.existsById(id)) {
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
        if(!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Категория с таким идентификатором не найдена!");
        }

        categoryRepository.removeById(id);
    }
}
