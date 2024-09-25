package ru.melowetty.controller.request;

import lombok.Data;

@Data
public class CategoryCreateRequest {
    public String slug;
    public String name;
}
