package ru.melowetty.command.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.melowetty.command.InitCommand;
import ru.melowetty.service.CategoryService;
import ru.melowetty.service.KudagoService;

@Qualifier("category_init")
@Component
@Slf4j
public class CategoryInitCommand implements InitCommand {
    private final CategoryService categoryService;
    private final KudagoService kudagoService;

    public CategoryInitCommand(CategoryService categoryService, KudagoService kudagoService) {
        this.categoryService = categoryService;
        this.kudagoService = kudagoService;
    }

    @Override
    public void execute() {
        log.info("Инициализация категорий запущена");
        var categories = kudagoService.getCategories();
        for (var category : categories) {
            categoryService.createCategory(category.slug, category.name);
        }

        log.info("Инициализация категорий окончена, теперь категорий: {}", categoryService.getCategories().size());
    }
}
