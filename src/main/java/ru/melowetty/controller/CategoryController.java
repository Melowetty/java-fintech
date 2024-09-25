package ru.melowetty.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.melowetty.controller.request.CategoryCreateRequest;
import ru.melowetty.controller.request.CategoryPutRequest;
import ru.melowetty.model.Category;
import ru.melowetty.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/v1/places/categories")
public class CategoryController {
    public final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(produces = "application/json")
    public List<Category> getCategories() {
        return categoryService.getCategories();
    }

    @GetMapping(path = "/{id}", produces = "application/json")
    public Category getCategoryById(@PathVariable int id) {
        return categoryService.getCategoryById(id);
    }

    @PostMapping(produces = "application/json")
    public Category createCategory(@RequestBody CategoryCreateRequest request) {
        return categoryService.createCategory(request.getSlug(), request.getName());
    }

    @PutMapping(path = "/{id}", produces = "application/json")
    public Category updateCategory(@PathVariable int id, @RequestBody CategoryPutRequest request) {
        return categoryService.updateCategory(id,request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable int id) {
        categoryService.deleteCategoryById(id);
    }
}
