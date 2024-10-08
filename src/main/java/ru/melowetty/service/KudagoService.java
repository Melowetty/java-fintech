package ru.melowetty.service;

import ru.melowetty.model.Category;
import ru.melowetty.model.Location;

import java.util.List;

public interface KudagoService {
    List<Category> getCategories();

    List<Location> getLocations();
}
