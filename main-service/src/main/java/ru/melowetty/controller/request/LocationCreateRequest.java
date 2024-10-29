package ru.melowetty.controller.request;

import lombok.Data;

@Data
public class LocationCreateRequest {
    public String slug;
    public String name;
}
