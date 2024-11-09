package ru.melowetty.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Generated;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Generated
public class EventCreateRequest {
    public String name;
    public LocalDateTime date;
    public Long placeId;
}
