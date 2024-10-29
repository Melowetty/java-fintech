package ru.melowetty.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.melowetty.controller.request.CategoryPutRequest;
import ru.melowetty.exception.EntityNotFoundException;
import ru.melowetty.model.Category;
import ru.melowetty.repository.CategoryRepository;
import ru.melowetty.service.impl.CategoryServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private KudagoService kudagoService;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    public void test_initialize() {
        Mockito.when(kudagoService.getCategories()).thenReturn(getCategories());

        categoryService.initialize();

        Mockito.verify(categoryRepository, Mockito.times(1)).create(getCategories().get(0));
        Mockito.verify(categoryRepository, Mockito.times(1)).create(getCategories().get(1));
        Mockito.verify(categoryRepository, Mockito.times(1)).create(getCategories().get(2));
    }

    @Test
    public void getCategories_returnsAllCategories() {
        List<Category> categories = getCategories();
        Mockito.when(categoryRepository.findAll()).thenReturn(categories);

        List<Category> result = categoryService.getCategories();

        assertEquals(categories.size(), result.size());
        assertEquals(categories, result);
    }

    @Test
    public void getCategoryById_existingId_returnsCategory() {
        Category category = getCategories().get(0);
        Mockito.when(categoryRepository.existsById(category.getId())).thenReturn(true);
        Mockito.when(categoryRepository.findById(category.getId())).thenReturn(category);

        Category result = categoryService.getCategoryById(category.getId());

        assertEquals(category, result);
    }

    @Test
    public void getCategoryById_nonExistingId_throwsException() {
        int nonExistingId = 999;
        Mockito.when(categoryRepository.existsById(nonExistingId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> categoryService.getCategoryById(nonExistingId));
    }

    @Test
    public void createCategory_validData_createsCategory() {
        String slug = "new-slug";
        String name = "new-name";
        Category category = new Category();
        category.setSlug(slug);
        category.setName(name);
        Mockito.when(categoryRepository.create(Mockito.any(Category.class))).thenReturn(category);

        Category result = categoryService.createCategory(slug, name);

        assertEquals(slug, result.getSlug());
        assertEquals(name, result.getName());
    }

    @Test
    public void updateCategory_existingId_updatesCategory() {
        int id = 1;
        CategoryPutRequest request = new CategoryPutRequest("updated-slug", "updated-name");
        Category category = new Category(id, request.slug, request.name);
        Mockito.when(categoryRepository.existsById(id)).thenReturn(true);
        Mockito.when(categoryRepository.update(Mockito.any(Category.class))).thenReturn(category);

        Category result = categoryService.updateCategory(id, request);

        assertEquals(id, result.getId());
        assertEquals(request.slug, result.getSlug());
        assertEquals(request.name, result.getName());
    }

    @Test
    public void updateCategory_nonExistingId_throwsException() {
        int nonExistingId = 999;
        CategoryPutRequest request = new CategoryPutRequest("updated-slug", "updated-name");
        Mockito.when(categoryRepository.existsById(nonExistingId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> categoryService.updateCategory(nonExistingId, request));
    }

    @Test
    public void deleteCategoryById_existingId_deletesCategory() {
        int id = 1;
        Mockito.when(categoryRepository.existsById(id)).thenReturn(true);

        categoryService.deleteCategoryById(id);

        Mockito.verify(categoryRepository, Mockito.times(1)).removeById(id);
    }

    @Test
    public void deleteCategoryById_nonExistingId_throwsException() {
        int nonExistingId = 999;
        Mockito.when(categoryRepository.existsById(nonExistingId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> categoryService.deleteCategoryById(nonExistingId));
    }

    private List<Category> getCategories() {
        return List.of(
                new Category(1, "test", "test-name"),
                new Category(2, "test2", "test-name2"),
                new Category(3, "test3", "test-nam3e"));
    }
}
