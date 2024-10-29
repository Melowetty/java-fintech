package ru.melowetty.controller.request;

import lombok.Data;

@Data
public class CategoryPutRequest {
    public String slug;
    public String name;
}
