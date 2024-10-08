package ru.melowetty.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.melowetty.controller.request.CategoryPutRequest;
import ru.melowetty.model.Category;
import ru.melowetty.service.CategoryService;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    public void getCategories_returnsListOfCategories() throws Exception {
        List<Category> categories = Arrays.asList(new Category(1, "slug1", "Category 1"), new Category(2, "slug2", "Category 2"));
        when(categoryService.getCategories()).thenReturn(categories);

        mockMvc.perform(get("/v1/places/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].slug").value("slug1"))
                .andExpect(jsonPath("$[0].name").value("Category 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].slug").value("slug2"))
                .andExpect(jsonPath("$[1].name").value("Category 2"));
    }

    @Test
    public void getCategoryById_existingId_returnsCategory() throws Exception {
        Category category = new Category(1, "slug1", "Category 1");
        when(categoryService.getCategoryById(1)).thenReturn(category);

        mockMvc.perform(get("/v1/places/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.slug").value("slug1"))
                .andExpect(jsonPath("$.name").value("Category 1"));
    }

    @Test
    public void updateCategory_existingId_returnsUpdatedCategory() throws Exception {
        CategoryPutRequest request = new CategoryPutRequest("New slug", "Updated Category");
        Category category = new Category(1, "New slug", "Updated Category");
        when(categoryService.updateCategory(1, request)).thenReturn(category);

        mockMvc.perform(put("/v1/places/categories/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\": \"New slug\", \"name\": \"Updated Category\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.slug").value("New slug"))
                .andExpect(jsonPath("$.name").value("Updated Category"));
    }

    @Test
    public void create_category() throws Exception {
        Category category = new Category(1, "slug", "name");
        when(categoryService.createCategory("slug", "name")).thenReturn(category);

        mockMvc.perform(post("/v1/places/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"slug\": \"slug\", \"name\": \"name\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.slug").value("slug"))
                .andExpect(jsonPath("$.name").value("name"));
    }

    @Test
    public void deleteCategoryById_existingId_deletesCategory() throws Exception {
        doNothing().when(categoryService).deleteCategoryById(1);

        mockMvc.perform(delete("/v1/places/categories/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategoryById(1);
    }
}
