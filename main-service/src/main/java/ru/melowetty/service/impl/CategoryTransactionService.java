package ru.melowetty.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.melowetty.event.EventListener;
import ru.melowetty.event.EventType;
import ru.melowetty.model.Category;
import ru.melowetty.repository.CategoryRepository;
import ru.melowetty.service.BackupService;

@Service
@Slf4j
public class CategoryTransactionService extends BackupService implements EventListener<Category> {
    private final CategoryRepository categoryRepository;

    public CategoryTransactionService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void handleUpdate(EventType eventType, Category event) {
        addChange(categoryRepository);
        log.info("Записано новое состояние категорий");
    }

    @Override
    public void rollback() {
        super.rollback();
        log.info("Был вызван откат операции");
    }
}
