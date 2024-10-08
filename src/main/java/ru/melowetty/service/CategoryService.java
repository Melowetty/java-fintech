package ru.melowetty.service;

import ru.melowetty.controller.request.CategoryPutRequest;
import ru.melowetty.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getCategories();
    Category getCategoryById(int id);
    Category createCategory(String slug, String name);
    Category updateCategory(int id, CategoryPutRequest request);
    void deleteCategoryById(int id);
}
