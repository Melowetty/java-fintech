package ru.melowetty.command.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.melowetty.command.InitCommand;
import ru.melowetty.repository.CategoryRepository;
import ru.melowetty.service.KudagoService;

@Qualifier("category_init")
@Component
@Slf4j
public class CategoryInitCommand implements InitCommand {
    private final CategoryRepository categoryRepository;
    private final KudagoService kudagoService;

    public CategoryInitCommand(CategoryRepository categoryRepository, KudagoService kudagoService) {
        this.categoryRepository = categoryRepository;
        this.kudagoService = kudagoService;
    }

    @Override
    public void execute() {
        log.info("Инициализация категорий запущена");
        var categories = kudagoService.getCategories();
        for (var category : categories) {
            categoryRepository.create(category);
        }

        log.info("Инициализация категорий окончена, теперь категорий: {}", categoryRepository.findAll().size());
    }
}
