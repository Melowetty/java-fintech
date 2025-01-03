package ru.melowetty.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.melowetty.utils.DateUtils;

import java.time.LocalDateTime;

public record EventShortDto(
        Long id,
        String name,
        @JsonFormat(pattern = DateUtils.DATE_TIME_PATTERN)
        LocalDateTime date,
        Long placeId
) {
}
