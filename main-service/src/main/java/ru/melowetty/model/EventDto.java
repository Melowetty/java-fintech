package ru.melowetty.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDto {
    public int id;

    public String title;

    public BigDecimal price;

    public List<EventDates> dates;

    @AllArgsConstructor
    public static class EventDates {
        public Instant start;
        public Instant end;
    }
}
