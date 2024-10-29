package ru.melowetty.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import ru.melowetty.annotation.Timed;
import ru.melowetty.command.InitCommand;
import ru.melowetty.controller.request.CategoryPutRequest;
import ru.melowetty.event.EventType;
import ru.melowetty.event.impl.CategoryEventManager;
import ru.melowetty.exception.EntityNotFoundException;
import ru.melowetty.model.Category;
import ru.melowetty.repository.CategoryRepository;
import ru.melowetty.service.CategoryService;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryEventManager eventManager;

    @Autowired
    @Qualifier("category_init")
    @Lazy
    private InitCommand initCommand;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryEventManager eventManager) {
        this.categoryRepository = categoryRepository;
        this.eventManager = eventManager;
        log.info("CategoryServiceImpl created");
    }

    @EventListener(ApplicationReadyEvent.class)
    @Timed
    public void initialize() {
        initCommand.execute();
    }

    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category getCategoryById(int id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Категория с таким идентификатором не найдена!");
        }
        return categoryRepository.findById(id);
    }

    @Override
    public Category createCategory(String slug, String name) {
        var category = new Category();
        category.setName(name);
        category.setSlug(slug);
        var newCategory = categoryRepository.create(category);
        eventManager.notify(EventType.CREATED, newCategory);
        return newCategory;
    }

    @Override
    public Category updateCategory(int id, CategoryPutRequest request) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Категория с таким идентификатором не найдена!");
        }
        var category = new Category();
        category.setId(id);
        category.setSlug(request.slug);
        category.setName(request.name);
        var newCategory = categoryRepository.update(category);
        eventManager.notify(EventType.CHANGED, newCategory);
        return newCategory;
    }

    @Override
    public void deleteCategoryById(int id) {
        if (!categoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Категория с таким идентификатором не найдена!");
        }

        var category = getCategoryById(id);
        categoryRepository.removeById(id);
        eventManager.notify(EventType.DELETED, category);
    }
}
