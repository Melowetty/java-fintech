package ru.melowetty.dto;

import java.util.List;

public record PlaceDto(
        Long id,
        String slug,
        String name,
        List<EventWithoutPlaceDto> events) {
}
